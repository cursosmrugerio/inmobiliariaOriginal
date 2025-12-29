package com.inmobiliaria.reporte.service;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import com.inmobiliaria.cobranza.domain.ProyeccionCobranza;
import com.inmobiliaria.cobranza.domain.SeguimientoCobranza;
import com.inmobiliaria.cobranza.repository.CarteraVencidaRepository;
import com.inmobiliaria.cobranza.repository.ProyeccionCobranzaRepository;
import com.inmobiliaria.cobranza.repository.SeguimientoCobranzaRepository;
import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.contrato.ContratoRepository;
import com.inmobiliaria.contrato.EstadoContrato;
import com.inmobiliaria.empresa.Empresa;
import com.inmobiliaria.empresa.EmpresaRepository;
import com.inmobiliaria.pago.Cargo;
import com.inmobiliaria.pago.CargoRepository;
import com.inmobiliaria.pago.Pago;
import com.inmobiliaria.pago.PagoRepository;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
    private final ContratoRepository contratoRepository;
    private final PagoRepository pagoRepository;
    private final CargoRepository cargoRepository;
    private final EmpresaRepository empresaRepository;

    private String getNombreEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .map(Empresa::getNombre)
                .orElse("Inmobiliaria");
    }

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
                .nombreEmpresa(getNombreEmpresa(empresaId))
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
                .nombreEmpresa(getNombreEmpresa(empresaId))
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
                .nombreEmpresa(getNombreEmpresa(empresaId))
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
                .nombreEmpresa(getNombreEmpresa(empresaId))
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

    // ========== FINIQUITO DE CONTRATO ==========

    public FiniquitoDTO generarFiniquito(Long contratoId) {
        Long empresaId = TenantContext.getCurrentTenant();

        Contrato contrato = contratoRepository.findByIdAndEmpresaId(contratoId, empresaId)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        Persona arrendatario = contrato.getArrendatario();
        Propiedad propiedad = contrato.getPropiedad();

        // Obtener cargos del contrato
        List<Cargo> cargos = cargoRepository.findByContratoIdAndEmpresaId(contratoId, empresaId);

        // Obtener pagos del contrato
        List<Pago> pagos = pagoRepository.findByContratoIdAndEmpresaId(contratoId, empresaId);

        // Calcular totales
        BigDecimal totalRentasPagadas = pagos.stream()
                .map(Pago::getMontoAplicado)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCargos = cargos.stream()
                .map(Cargo::getMontoOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPendiente = cargos.stream()
                .map(Cargo::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRentasPendientes = saldoPendiente;

        // Calcular depósito
        BigDecimal montoDeposito = contrato.getMontoDeposito() != null ? contrato.getMontoDeposito() : BigDecimal.ZERO;
        BigDecimal deduccionesDeposito = saldoPendiente.min(montoDeposito);
        BigDecimal depositoADevolver = montoDeposito.subtract(deduccionesDeposito);

        // Monto de liquidación (positivo = debe el arrendatario, negativo = se le devuelve)
        BigDecimal montoLiquidacion = saldoPendiente.subtract(montoDeposito);
        if (montoLiquidacion.compareTo(BigDecimal.ZERO) < 0) {
            montoLiquidacion = BigDecimal.ZERO;
        }

        // Crear lista de conceptos
        List<FiniquitoDTO.ConceptoFiniquitoDTO> conceptos = new ArrayList<>();

        // Agregar cargos
        for (Cargo cargo : cargos) {
            conceptos.add(FiniquitoDTO.ConceptoFiniquitoDTO.builder()
                    .concepto(cargo.getConcepto())
                    .tipo("CARGO")
                    .fecha(cargo.getFechaCargo())
                    .monto(cargo.getMontoOriginal())
                    .estado(cargo.getEstado().name())
                    .notas(cargo.getNotas())
                    .build());
        }

        // Agregar pagos
        for (Pago pago : pagos) {
            conceptos.add(FiniquitoDTO.ConceptoFiniquitoDTO.builder()
                    .concepto("Pago - " + pago.getNumeroRecibo())
                    .tipo("ABONO")
                    .fecha(pago.getFechaPago())
                    .monto(pago.getMonto())
                    .estado(pago.getEstado().name())
                    .notas(pago.getNotas())
                    .build());
        }

        // Agregar depósito
        if (montoDeposito.compareTo(BigDecimal.ZERO) > 0) {
            conceptos.add(FiniquitoDTO.ConceptoFiniquitoDTO.builder()
                    .concepto("Depósito en garantía")
                    .tipo("DEPOSITO")
                    .fecha(contrato.getFechaInicio())
                    .monto(montoDeposito)
                    .estado("REGISTRADO")
                    .build());

            if (deduccionesDeposito.compareTo(BigDecimal.ZERO) > 0) {
                conceptos.add(FiniquitoDTO.ConceptoFiniquitoDTO.builder()
                        .concepto("Deducción de depósito por adeudos")
                        .tipo("DEDUCCION")
                        .fecha(LocalDate.now())
                        .monto(deduccionesDeposito.negate())
                        .estado("APLICADO")
                        .build());
            }
        }

        // Ordenar por fecha
        conceptos.sort(Comparator.comparing(FiniquitoDTO.ConceptoFiniquitoDTO::getFecha));

        return FiniquitoDTO.builder()
                .contratoId(contratoId)
                .numeroContrato(contrato.getNumeroContrato())
                .empresaId(empresaId)
                .nombreEmpresa(getNombreEmpresa(empresaId))
                .arrendatarioId(arrendatario.getId())
                .nombreArrendatario(arrendatario.getNombreCompleto())
                .rfcArrendatario(arrendatario.getRfc())
                .emailArrendatario(arrendatario.getEmail())
                .telefonoArrendatario(arrendatario.getTelefono())
                .propiedadId(propiedad.getId())
                .direccionPropiedad(propiedad.getDireccionCompleta())
                .tipoPropiedad(propiedad.getTipoPropiedad() != null ? propiedad.getTipoPropiedad().getNombre() : "")
                .fechaInicioContrato(contrato.getFechaInicio())
                .fechaFinContrato(contrato.getFechaFin())
                .fechaTerminacion(LocalDate.now())
                .motivoTerminacion(contrato.getEstado() != null ? contrato.getEstado().name() : "")
                .montoRentaMensual(contrato.getMontoRenta())
                .montoDeposito(montoDeposito)
                .totalRentasPagadas(totalRentasPagadas)
                .totalRentasPendientes(totalRentasPendientes)
                .totalCargosAdicionales(BigDecimal.ZERO)
                .totalPagosRealizados(totalRentasPagadas)
                .saldoPendiente(saldoPendiente)
                .depositoADevolver(depositoADevolver)
                .deduccionesDeposito(deduccionesDeposito)
                .montoLiquidacion(montoLiquidacion)
                .conceptos(conceptos)
                .fechaGeneracion(LocalDate.now())
                .generadoPor("Sistema")
                .build();
    }

    // ========== REPORTE MENSUAL ==========

    public ReporteMensualDTO generarReporteMensual(Integer mes, Integer anio) {
        Long empresaId = TenantContext.getCurrentTenant();

        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate inicioMes = periodo.atDay(1);
        LocalDate finMes = periodo.atEndOfMonth();

        String periodoDescripcion = periodo.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                + " " + anio;

        // Propiedades
        List<Propiedad> propiedades = propiedadRepository.findByEmpresaIdAndActivoTrue(empresaId);
        int totalPropiedades = propiedades.size();

        // Contratos
        List<Contrato> todosContratos = contratoRepository.findByEmpresaId(empresaId);
        List<Contrato> contratosActivos = contratoRepository.findByEmpresaIdAndEstado(empresaId, EstadoContrato.ACTIVO);
        List<Contrato> contratosPorVencer = contratoRepository.findContratosPorVencer(empresaId, finMes.plusDays(30));
        List<Contrato> contratosVencidos = contratoRepository.findContratosVencidos(empresaId, finMes);

        int propiedadesOcupadas = (int) contratosActivos.stream()
                .map(c -> c.getPropiedad().getId())
                .distinct()
                .count();
        int propiedadesDisponibles = totalPropiedades - propiedadesOcupadas;

        BigDecimal porcentajeOcupacion = BigDecimal.ZERO;
        if (totalPropiedades > 0) {
            porcentajeOcupacion = BigDecimal.valueOf(propiedadesOcupadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalPropiedades), 2, RoundingMode.HALF_UP);
        }

        // Pagos del mes
        List<Pago> pagosDelMes = pagoRepository.findPagosByPeriodo(empresaId, inicioMes, finMes);

        BigDecimal ingresosPorRenta = pagosDelMes.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Renta esperada (sum of all active contracts' monthly rent)
        BigDecimal rentaEsperada = contratosActivos.stream()
                .map(Contrato::getMontoRenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal porcentajeCobranza = BigDecimal.ZERO;
        if (rentaEsperada.compareTo(BigDecimal.ZERO) > 0) {
            porcentajeCobranza = ingresosPorRenta.multiply(BigDecimal.valueOf(100))
                    .divide(rentaEsperada, 2, RoundingMode.HALF_UP);
        }

        // Cartera
        List<CarteraVencida> cartera = carteraVencidaRepository.findByEmpresaIdAndActivoTrue(empresaId);

        BigDecimal carteraVigente = cartera.stream()
                .filter(c -> c.getDiasVencido() != null && c.getDiasVencido() <= 0)
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carteraVencida = cartera.stream()
                .filter(c -> c.getDiasVencido() != null && c.getDiasVencido() > 0)
                .map(CarteraVencida::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carteraTotal = carteraVigente.add(carteraVencida);

        int clientesConAdeudo = (int) cartera.stream()
                .filter(c -> c.getMontoPendiente().compareTo(BigDecimal.ZERO) > 0)
                .map(CarteraVencida::getPersonaId)
                .distinct()
                .count();

        // Detalle por propiedad
        List<ReporteMensualDTO.PropiedadMensualDTO> detallePropiedades = new ArrayList<>();
        for (Propiedad prop : propiedades) {
            Optional<Contrato> contratoActivo = contratosActivos.stream()
                    .filter(c -> c.getPropiedad().getId().equals(prop.getId()))
                    .findFirst();

            String estadoOcupacion = contratoActivo.isPresent() ? "OCUPADA" : "DISPONIBLE";
            String arrendatarioNombre = contratoActivo.map(c -> c.getArrendatario().getNombreCompleto()).orElse("-");
            BigDecimal rentaMensual = contratoActivo.map(Contrato::getMontoRenta).orElse(BigDecimal.ZERO);

            // Buscar pagos de esta propiedad en el mes
            BigDecimal rentaCobrada = pagosDelMes.stream()
                    .filter(p -> p.getContrato().getPropiedad().getId().equals(prop.getId()))
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = rentaMensual.subtract(rentaCobrada);
            String estadoPago = saldoPendiente.compareTo(BigDecimal.ZERO) <= 0 ? "AL_CORRIENTE" : "PENDIENTE";

            detallePropiedades.add(ReporteMensualDTO.PropiedadMensualDTO.builder()
                    .propiedadId(prop.getId())
                    .direccion(prop.getDireccionCompleta())
                    .tipoPropiedad(prop.getTipoPropiedad() != null ? prop.getTipoPropiedad().getNombre() : "")
                    .estadoOcupacion(estadoOcupacion)
                    .arrendatario(arrendatarioNombre)
                    .rentaMensual(rentaMensual)
                    .rentaCobrada(rentaCobrada)
                    .saldoPendiente(saldoPendiente.max(BigDecimal.ZERO))
                    .estadoPago(estadoPago)
                    .build());
        }

        // Detalle de ingresos
        List<ReporteMensualDTO.IngresoMensualDTO> detalleIngresos = new ArrayList<>();
        for (Pago pago : pagosDelMes) {
            detalleIngresos.add(ReporteMensualDTO.IngresoMensualDTO.builder()
                    .fecha(pago.getFechaPago())
                    .concepto("Pago - " + pago.getNumeroRecibo())
                    .propiedad(pago.getContrato().getPropiedad().getDireccionCompleta())
                    .cliente(pago.getPersona().getNombreCompleto())
                    .monto(pago.getMonto())
                    .tipoPago(pago.getTipoPago() != null ? pago.getTipoPago().name() : "")
                    .referencia(pago.getReferencia())
                    .build());
        }

        // Top 5 morosos
        List<ReporteMensualDTO.MorosoDTO> topMorosos = cartera.stream()
                .filter(c -> c.getMontoPendiente().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getMontoPendiente().compareTo(a.getMontoPendiente()))
                .limit(5)
                .map(cv -> {
                    Persona persona = personaRepository.findByIdAndEmpresaId(cv.getPersonaId(), empresaId).orElse(null);
                    Propiedad prop = propiedadRepository.findByIdAndEmpresaId(cv.getPropiedadId(), empresaId).orElse(null);
                    return ReporteMensualDTO.MorosoDTO.builder()
                            .personaId(cv.getPersonaId())
                            .nombre(persona != null ? persona.getNombreCompleto() : "")
                            .propiedad(prop != null ? prop.getDireccionCompleta() : "")
                            .montoAdeudado(cv.getMontoPendiente())
                            .diasVencido(cv.getDiasVencido() != null ? cv.getDiasVencido() : 0)
                            .estadoCobranza(cv.getEstadoCobranza() != null ? cv.getEstadoCobranza().name() : "")
                            .build();
                })
                .toList();

        return ReporteMensualDTO.builder()
                .empresaId(empresaId)
                .nombreEmpresa(getNombreEmpresa(empresaId))
                .mes(mes)
                .anio(anio)
                .periodoDescripcion(periodoDescripcion)
                .fechaGeneracion(LocalDate.now())
                .totalPropiedades(totalPropiedades)
                .propiedadesOcupadas(propiedadesOcupadas)
                .propiedadesDisponibles(propiedadesDisponibles)
                .porcentajeOcupacion(porcentajeOcupacion)
                .contratosActivos(contratosActivos.size())
                .contratosPorVencer(contratosPorVencer.size())
                .contratosVencidos(contratosVencidos.size())
                .contratosNuevos(0) // Would need to track creation date
                .contratosTerminados(0)
                .contratosRenovados(0)
                .ingresosPorRenta(ingresosPorRenta)
                .ingresosPorOtrosConceptos(BigDecimal.ZERO)
                .totalIngresos(ingresosPorRenta)
                .rentaEsperada(rentaEsperada)
                .rentaCobrada(ingresosPorRenta)
                .porcentajeCobranza(porcentajeCobranza)
                .carteraVigente(carteraVigente)
                .carteraVencida(carteraVencida)
                .carteraTotal(carteraTotal)
                .clientesConAdeudo(clientesConAdeudo)
                .clientesAlCorriente(contratosActivos.size() - clientesConAdeudo)
                .detallePropiedades(detallePropiedades)
                .detalleIngresos(detalleIngresos)
                .topMorosos(topMorosos)
                .build();
    }
}
