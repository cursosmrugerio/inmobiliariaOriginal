package com.inmobiliaria.reporte.service;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import com.inmobiliaria.cobranza.domain.ProyeccionCobranza;
import com.inmobiliaria.cobranza.domain.SeguimientoCobranza;
import com.inmobiliaria.cobranza.repository.CarteraVencidaRepository;
import com.inmobiliaria.cobranza.repository.ProyeccionCobranzaRepository;
import com.inmobiliaria.cobranza.repository.SeguimientoCobranzaRepository;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.propiedad.PropiedadRepository;
import com.inmobiliaria.reporte.dto.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final CarteraVencidaRepository carteraVencidaRepository;
    private final ProyeccionCobranzaRepository proyeccionCobranzaRepository;
    private final SeguimientoCobranzaRepository seguimientoCobranzaRepository;
    private final PersonaRepository personaRepository;
    private final PropiedadRepository propiedadRepository;

    // ========== ESTADO DE CUENTA (#39) ==========

    public EstadoCuentaDTO generarEstadoCuenta(Long personaId, LocalDate fechaInicio, LocalDate fechaFin) {
        Long empresaId = TenantContext.getCurrentTenant();

        Persona persona = personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        List<CarteraVencida> cartera = carteraVencidaRepository.findByEmpresaIdAndPersonaIdAndActivoTrue(empresaId, personaId);

        // Filtrar por periodo si se especifica
        if (fechaInicio != null && fechaFin != null) {
            cartera = cartera.stream()
                    .filter(c -> !c.getFechaVencimiento().isBefore(fechaInicio) && !c.getFechaVencimiento().isAfter(fechaFin))
                    .toList();
        }

        // Crear movimientos
        List<EstadoCuentaItemDTO> movimientos = new ArrayList<>();
        BigDecimal saldoAcumulado = BigDecimal.ZERO;

        for (CarteraVencida cv : cartera.stream()
                .sorted(Comparator.comparing(CarteraVencida::getFechaVencimiento))
                .toList()) {

            saldoAcumulado = saldoAcumulado.add(cv.getMontoOriginal());

            movimientos.add(EstadoCuentaItemDTO.builder()
                    .fecha(cv.getFechaVencimiento())
                    .concepto(cv.getConcepto())
                    .tipo("CARGO")
                    .cargo(cv.getMontoOriginal())
                    .abono(BigDecimal.ZERO)
                    .saldo(saldoAcumulado)
                    .referencia(cv.getId().toString())
                    .diasVencido(cv.getDiasVencido())
                    .estado(cv.getEstadoCobranza() != null ? cv.getEstadoCobranza().name() : null)
                    .build());

            // Si hay pago parcial, agregar abono
            BigDecimal pagado = cv.getMontoOriginal().subtract(cv.getMontoPendiente());
            if (pagado.compareTo(BigDecimal.ZERO) > 0) {
                saldoAcumulado = saldoAcumulado.subtract(pagado);
                movimientos.add(EstadoCuentaItemDTO.builder()
                        .fecha(cv.getUpdatedAt().toLocalDate())
                        .concepto("Pago - " + cv.getConcepto())
                        .tipo("ABONO")
                        .cargo(BigDecimal.ZERO)
                        .abono(pagado)
                        .saldo(saldoAcumulado)
                        .referencia(cv.getId().toString())
                        .diasVencido(0)
                        .estado("PAGADO")
                        .build());
            }
        }

        // Calcular totales
        BigDecimal totalCargos = cartera.stream()
                .map(CarteraVencida::getMontoOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAbonos = cartera.stream()
                .map(c -> c.getMontoOriginal().subtract(c.getMontoPendiente()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoActual = cartera.stream()
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoVencido = cartera.stream()
                .filter(c -> c.getDiasVencido() != null && c.getDiasVencido() > 0)
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPorVencer = saldoActual.subtract(saldoVencido);

        // Obtener propiedades relacionadas
        List<String> propiedades = cartera.stream()
                .map(CarteraVencida::getPropiedadId)
                .distinct()
                .map(propId -> propiedadRepository.findByIdAndEmpresaId(propId, empresaId)
                        .map(Propiedad::getDireccionCompleta)
                        .orElse("Propiedad ID: " + propId))
                .toList();

        // Obtener dirección principal
        String direccion = persona.getDirecciones().stream()
                .findFirst()
                .map(d -> d.getCalle() + " " + d.getNumeroExterior())
                .orElse("");

        return EstadoCuentaDTO.builder()
                .personaId(personaId)
                .nombreCliente(persona.getNombreCompleto())
                .tipoPersona(persona.getTipoPersona().name())
                .rfc(persona.getRfc())
                .email(persona.getEmail())
                .telefono(persona.getTelefono())
                .direccion(direccion)
                .empresaId(empresaId)
                .nombreEmpresa("Inmobiliaria") // TODO: Get from empresa entity
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaGeneracion(LocalDate.now())
                .saldoAnterior(BigDecimal.ZERO)
                .totalCargos(totalCargos)
                .totalAbonos(totalAbonos)
                .saldoActual(saldoActual)
                .saldoVencido(saldoVencido)
                .saldoPorVencer(saldoPorVencer)
                .movimientos(movimientos)
                .propiedades(propiedades)
                .build();
    }

    // ========== ANTIGÜEDAD DE SALDOS (#40) ==========

    public AntiguedadSaldosDTO generarAntiguedadSaldos(LocalDate fechaCorte) {
        Long empresaId = TenantContext.getCurrentTenant();

        if (fechaCorte == null) {
            fechaCorte = LocalDate.now();
        }

        List<CarteraVencida> cartera = carteraVencidaRepository.findByEmpresaIdAndActivoTrue(empresaId);

        // Agrupar por persona
        Map<Long, List<CarteraVencida>> porPersona = cartera.stream()
                .collect(Collectors.groupingBy(CarteraVencida::getPersonaId));

        List<AntiguedadSaldosItemDTO> detalle = new ArrayList<>();

        BigDecimal totalVigente = BigDecimal.ZERO;
        BigDecimal totalVencido1a30 = BigDecimal.ZERO;
        BigDecimal totalVencido31a60 = BigDecimal.ZERO;
        BigDecimal totalVencido61a90 = BigDecimal.ZERO;
        BigDecimal totalVencidoMas90 = BigDecimal.ZERO;

        for (Map.Entry<Long, List<CarteraVencida>> entry : porPersona.entrySet()) {
            Long personaId = entry.getKey();
            List<CarteraVencida> cuentas = entry.getValue();

            Persona persona = personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                    .orElse(null);
            String nombreCliente = persona != null ? persona.getNombreCompleto() : "Cliente ID: " + personaId;

            // Calcular montos por clasificación
            BigDecimal vigente = sumByClasificacion(cuentas, CarteraVencida.ClasificacionAntiguedad.VIGENTE);
            BigDecimal vencido1a30 = sumByClasificacion(cuentas, CarteraVencida.ClasificacionAntiguedad.VENCIDO_1_30);
            BigDecimal vencido31a60 = sumByClasificacion(cuentas, CarteraVencida.ClasificacionAntiguedad.VENCIDO_31_60);
            BigDecimal vencido61a90 = sumByClasificacion(cuentas, CarteraVencida.ClasificacionAntiguedad.VENCIDO_61_90);
            BigDecimal vencidoMas90 = sumByClasificacion(cuentas, CarteraVencida.ClasificacionAntiguedad.VENCIDO_MAS_90);

            BigDecimal totalVenc = vencido1a30.add(vencido31a60).add(vencido61a90).add(vencidoMas90);
            BigDecimal saldoTotal = vigente.add(totalVenc);

            // Obtener propiedad principal
            Long propiedadId = cuentas.get(0).getPropiedadId();
            String direccionPropiedad = propiedadRepository.findByIdAndEmpresaId(propiedadId, empresaId)
                    .map(Propiedad::getDireccionCompleta)
                    .orElse("");

            detalle.add(AntiguedadSaldosItemDTO.builder()
                    .personaId(personaId)
                    .nombreCliente(nombreCliente)
                    .propiedadId(propiedadId)
                    .direccionPropiedad(direccionPropiedad)
                    .vigente(vigente)
                    .vencido1a30(vencido1a30)
                    .vencido31a60(vencido31a60)
                    .vencido61a90(vencido61a90)
                    .vencidoMas90(vencidoMas90)
                    .totalVencido(totalVenc)
                    .saldoTotal(saldoTotal)
                    .cantidadDocumentos(cuentas.size())
                    .build());

            // Acumular totales
            totalVigente = totalVigente.add(vigente);
            totalVencido1a30 = totalVencido1a30.add(vencido1a30);
            totalVencido31a60 = totalVencido31a60.add(vencido31a60);
            totalVencido61a90 = totalVencido61a90.add(vencido61a90);
            totalVencidoMas90 = totalVencidoMas90.add(vencidoMas90);
        }

        BigDecimal totalVencido = totalVencido1a30.add(totalVencido31a60).add(totalVencido61a90).add(totalVencidoMas90);
        BigDecimal totalGeneral = totalVigente.add(totalVencido);

        // Calcular porcentajes
        BigDecimal porcentajeVigente = BigDecimal.ZERO;
        BigDecimal porcentajeVencido = BigDecimal.ZERO;
        if (totalGeneral.compareTo(BigDecimal.ZERO) > 0) {
            porcentajeVigente = totalVigente.multiply(BigDecimal.valueOf(100)).divide(totalGeneral, 2, RoundingMode.HALF_UP);
            porcentajeVencido = totalVencido.multiply(BigDecimal.valueOf(100)).divide(totalGeneral, 2, RoundingMode.HALF_UP);
        }

        return AntiguedadSaldosDTO.builder()
                .empresaId(empresaId)
                .nombreEmpresa("Inmobiliaria")
                .fechaCorte(fechaCorte)
                .fechaGeneracion(LocalDate.now())
                .totalVigente(totalVigente)
                .totalVencido1a30(totalVencido1a30)
                .totalVencido31a60(totalVencido31a60)
                .totalVencido61a90(totalVencido61a90)
                .totalVencidoMas90(totalVencidoMas90)
                .totalVencido(totalVencido)
                .totalGeneral(totalGeneral)
                .cantidadClientes(porPersona.size())
                .cantidadDocumentos(cartera.size())
                .porcentajeVigente(porcentajeVigente)
                .porcentajeVencido(porcentajeVencido)
                .detalle(detalle)
                .build();
    }

    private BigDecimal sumByClasificacion(List<CarteraVencida> cuentas, CarteraVencida.ClasificacionAntiguedad clasificacion) {
        return cuentas.stream()
                .filter(c -> c.getClasificacionAntiguedad() == clasificacion)
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ========== CARTERA VENCIDA (#41) ==========

    public ReporteCarteraVencidaDTO generarReporteCarteraVencida(LocalDate fechaCorte) {
        Long empresaId = TenantContext.getCurrentTenant();

        if (fechaCorte == null) {
            fechaCorte = LocalDate.now();
        }

        List<CarteraVencida> cartera = carteraVencidaRepository.findByEmpresaIdAndActivoTrue(empresaId);

        // Crear detalle
        List<ReporteCarteraVencidaDTO.CarteraVencidaItemDTO> detalle = new ArrayList<>();

        for (CarteraVencida cv : cartera) {
            Persona persona = personaRepository.findByIdAndEmpresaId(cv.getPersonaId(), empresaId).orElse(null);
            Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(cv.getPropiedadId(), empresaId).orElse(null);

            // Obtener última gestión
            List<SeguimientoCobranza> seguimientos = seguimientoCobranzaRepository
                    .findByCarteraVencidaIdAndActivoTrueOrderByFechaContactoDesc(cv.getId());
            String ultimaGestion = "";
            LocalDate fechaUltimaGestion = null;
            if (!seguimientos.isEmpty()) {
                SeguimientoCobranza ultimo = seguimientos.get(0);
                ultimaGestion = ultimo.getDescripcion();
                fechaUltimaGestion = ultimo.getFechaContacto().toLocalDate();
            }

            BigDecimal montoTotal = cv.getMontoPendiente();
            if (cv.getMontoPenalidad() != null) {
                montoTotal = montoTotal.add(cv.getMontoPenalidad());
            }

            detalle.add(ReporteCarteraVencidaDTO.CarteraVencidaItemDTO.builder()
                    .id(cv.getId())
                    .personaId(cv.getPersonaId())
                    .nombreCliente(persona != null ? persona.getNombreCompleto() : "")
                    .propiedadId(cv.getPropiedadId())
                    .direccionPropiedad(propiedad != null ? propiedad.getDireccionCompleta() : "")
                    .concepto(cv.getConcepto())
                    .fechaVencimiento(cv.getFechaVencimiento())
                    .diasVencido(cv.getDiasVencido())
                    .clasificacion(cv.getClasificacionAntiguedad() != null ? cv.getClasificacionAntiguedad().name() : "")
                    .montoOriginal(cv.getMontoOriginal())
                    .montoPendiente(cv.getMontoPendiente())
                    .montoPenalidad(cv.getMontoPenalidad())
                    .montoTotal(montoTotal)
                    .estadoCobranza(cv.getEstadoCobranza() != null ? cv.getEstadoCobranza().name() : "")
                    .ultimaGestion(ultimaGestion)
                    .fechaUltimaGestion(fechaUltimaGestion)
                    .build());
        }

        // Calcular totales
        BigDecimal totalCartera = cartera.stream()
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPenalidades = cartera.stream()
                .map(c -> c.getMontoPenalidad() != null ? c.getMontoPenalidad() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Por estado
        Map<CarteraVencida.EstadoCobranza, List<CarteraVencida>> porEstado = cartera.stream()
                .collect(Collectors.groupingBy(c -> c.getEstadoCobranza() != null ? c.getEstadoCobranza() : CarteraVencida.EstadoCobranza.PENDIENTE));

        // Por clasificación
        BigDecimal montoVigente = sumByClasificacion(cartera, CarteraVencida.ClasificacionAntiguedad.VIGENTE);
        BigDecimal monto1a30 = sumByClasificacion(cartera, CarteraVencida.ClasificacionAntiguedad.VENCIDO_1_30);
        BigDecimal monto31a60 = sumByClasificacion(cartera, CarteraVencida.ClasificacionAntiguedad.VENCIDO_31_60);
        BigDecimal monto61a90 = sumByClasificacion(cartera, CarteraVencida.ClasificacionAntiguedad.VENCIDO_61_90);
        BigDecimal montoMas90 = sumByClasificacion(cartera, CarteraVencida.ClasificacionAntiguedad.VENCIDO_MAS_90);

        return ReporteCarteraVencidaDTO.builder()
                .empresaId(empresaId)
                .nombreEmpresa("Inmobiliaria")
                .fechaCorte(fechaCorte)
                .fechaGeneracion(LocalDate.now())
                .totalCartera(totalCartera)
                .totalPenalidades(totalPenalidades)
                .totalGeneral(totalCartera.add(totalPenalidades))
                .cantidadCuentas(cartera.size())
                .cuentasPendientes(countByEstado(porEstado, CarteraVencida.EstadoCobranza.PENDIENTE))
                .cuentasEnGestion(countByEstado(porEstado, CarteraVencida.EstadoCobranza.EN_GESTION))
                .cuentasConPromesa(countByEstado(porEstado, CarteraVencida.EstadoCobranza.PROMESA_PAGO))
                .cuentasParcialmentePagadas(countByEstado(porEstado, CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO))
                .montoPendiente(sumByEstado(porEstado, CarteraVencida.EstadoCobranza.PENDIENTE))
                .montoEnGestion(sumByEstado(porEstado, CarteraVencida.EstadoCobranza.EN_GESTION))
                .montoConPromesa(sumByEstado(porEstado, CarteraVencida.EstadoCobranza.PROMESA_PAGO))
                .montoParcialmentePagado(sumByEstado(porEstado, CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO))
                .montoVigente(montoVigente)
                .monto1a30(monto1a30)
                .monto31a60(monto31a60)
                .monto61a90(monto61a90)
                .montoMas90(montoMas90)
                .detalle(detalle)
                .build();
    }

    private int countByEstado(Map<CarteraVencida.EstadoCobranza, List<CarteraVencida>> map, CarteraVencida.EstadoCobranza estado) {
        return map.getOrDefault(estado, Collections.emptyList()).size();
    }

    private BigDecimal sumByEstado(Map<CarteraVencida.EstadoCobranza, List<CarteraVencida>> map, CarteraVencida.EstadoCobranza estado) {
        return map.getOrDefault(estado, Collections.emptyList()).stream()
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ========== PROYECCIÓN DE COBRANZA (#42) ==========

    public ProyeccionCobranzaReporteDTO generarReporteProyeccion(LocalDate periodoInicio, LocalDate periodoFin) {
        Long empresaId = TenantContext.getCurrentTenant();

        List<ProyeccionCobranza> proyecciones = proyeccionCobranzaRepository
                .findByEmpresaIdAndPeriodoBetweenAndActivoTrueOrderByPeriodoAsc(empresaId, periodoInicio, periodoFin);

        List<ProyeccionCobranzaReporteDTO.ProyeccionMesDTO> detalleMensual = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "ES"));

        BigDecimal totalProyectado = BigDecimal.ZERO;
        BigDecimal totalCobrado = BigDecimal.ZERO;
        int totalPagosEsperados = 0;
        int totalPagosRecibidos = 0;
        int totalContratos = 0;

        for (ProyeccionCobranza p : proyecciones) {
            BigDecimal proyectado = p.getMontoProyectado() != null ? p.getMontoProyectado() : BigDecimal.ZERO;
            BigDecimal cobrado = p.getMontoCobrado() != null ? p.getMontoCobrado() : BigDecimal.ZERO;
            BigDecimal diferencia = proyectado.subtract(cobrado);

            BigDecimal porcentaje = BigDecimal.ZERO;
            if (proyectado.compareTo(BigDecimal.ZERO) > 0) {
                porcentaje = cobrado.multiply(BigDecimal.valueOf(100)).divide(proyectado, 2, RoundingMode.HALF_UP);
            }

            detalleMensual.add(ProyeccionCobranzaReporteDTO.ProyeccionMesDTO.builder()
                    .periodo(p.getPeriodo())
                    .mesAnio(p.getPeriodo().format(formatter))
                    .montoProyectado(proyectado)
                    .montoCobrado(cobrado)
                    .diferencia(diferencia)
                    .porcentajeCumplimiento(porcentaje)
                    .cantidadContratos(p.getCantidadContratos() != null ? p.getCantidadContratos() : 0)
                    .pagosEsperados(p.getCantidadPagosEsperados() != null ? p.getCantidadPagosEsperados() : 0)
                    .pagosRecibidos(p.getCantidadPagosRecibidos() != null ? p.getCantidadPagosRecibidos() : 0)
                    .build());

            totalProyectado = totalProyectado.add(proyectado);
            totalCobrado = totalCobrado.add(cobrado);
            totalPagosEsperados += p.getCantidadPagosEsperados() != null ? p.getCantidadPagosEsperados() : 0;
            totalPagosRecibidos += p.getCantidadPagosRecibidos() != null ? p.getCantidadPagosRecibidos() : 0;
            if (p.getCantidadContratos() != null && p.getCantidadContratos() > totalContratos) {
                totalContratos = p.getCantidadContratos();
            }
        }

        BigDecimal porcentajeCumplimiento = BigDecimal.ZERO;
        if (totalProyectado.compareTo(BigDecimal.ZERO) > 0) {
            porcentajeCumplimiento = totalCobrado.multiply(BigDecimal.valueOf(100)).divide(totalProyectado, 2, RoundingMode.HALF_UP);
        }

        return ProyeccionCobranzaReporteDTO.builder()
                .empresaId(empresaId)
                .nombreEmpresa("Inmobiliaria")
                .periodoInicio(periodoInicio)
                .periodoFin(periodoFin)
                .fechaGeneracion(LocalDate.now())
                .totalProyectado(totalProyectado)
                .totalCobrado(totalCobrado)
                .totalPendiente(totalProyectado.subtract(totalCobrado))
                .porcentajeCumplimiento(porcentajeCumplimiento)
                .totalContratosActivos(totalContratos)
                .totalPagosEsperados(totalPagosEsperados)
                .totalPagosRecibidos(totalPagosRecibidos)
                .detalleMensual(detalleMensual)
                .build();
    }
}
