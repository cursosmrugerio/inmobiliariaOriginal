package com.inmobiliaria.cobranza.service;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import com.inmobiliaria.cobranza.domain.ProyeccionCobranza;
import com.inmobiliaria.cobranza.domain.SeguimientoCobranza;
import com.inmobiliaria.cobranza.dto.*;
import com.inmobiliaria.cobranza.repository.CarteraVencidaRepository;
import com.inmobiliaria.cobranza.repository.ProyeccionCobranzaRepository;
import com.inmobiliaria.cobranza.repository.SeguimientoCobranzaRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CobranzaService {

    private final CarteraVencidaRepository carteraVencidaRepository;
    private final SeguimientoCobranzaRepository seguimientoCobranzaRepository;
    private final ProyeccionCobranzaRepository proyeccionCobranzaRepository;

    // ========== CARTERA VENCIDA ==========

    public List<CarteraVencidaDTO> getAllCarteraVencida(boolean activeOnly) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<CarteraVencida> cartera = activeOnly
                ? carteraVencidaRepository.findByEmpresaIdAndActivoTrue(empresaId)
                : carteraVencidaRepository.findByEmpresaId(empresaId);
        return cartera.stream().map(CarteraVencidaDTO::fromEntity).toList();
    }

    public CarteraVencidaDTO getCarteraVencidaById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada con ID: " + id));
        return CarteraVencidaDTO.fromEntity(cartera);
    }

    public CarteraVencidaDTO createCarteraVencida(CreateCarteraVencidaRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        CarteraVencida cartera = CarteraVencida.builder()
                .empresaId(empresaId)
                .contratoId(request.getContratoId())
                .personaId(request.getPersonaId())
                .propiedadId(request.getPropiedadId())
                .montoOriginal(request.getMontoOriginal())
                .montoPendiente(request.getMontoOriginal())
                .fechaVencimiento(request.getFechaVencimiento())
                .concepto(request.getConcepto())
                .porcentajePenalidad(request.getPorcentajePenalidad() != null ? request.getPorcentajePenalidad() : BigDecimal.ZERO)
                .montoPenalidad(BigDecimal.ZERO)
                .build();

        CarteraVencida saved = carteraVencidaRepository.save(cartera);
        return CarteraVencidaDTO.fromEntity(saved);
    }

    public CarteraVencidaDTO updateEstadoCobranza(Long id, String estadoCobranza) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada con ID: " + id));

        cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.valueOf(estadoCobranza));
        CarteraVencida saved = carteraVencidaRepository.save(cartera);
        return CarteraVencidaDTO.fromEntity(saved);
    }

    public CarteraVencidaDTO registrarPago(Long id, BigDecimal montoPago) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada con ID: " + id));

        BigDecimal nuevoMontoPendiente = cartera.getMontoPendiente().subtract(montoPago);
        if (nuevoMontoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            nuevoMontoPendiente = BigDecimal.ZERO;
        }

        cartera.setMontoPendiente(nuevoMontoPendiente);

        if (nuevoMontoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PAGADO);
        } else if (nuevoMontoPendiente.compareTo(cartera.getMontoOriginal()) < 0) {
            cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO);
        }

        CarteraVencida saved = carteraVencidaRepository.save(cartera);
        return CarteraVencidaDTO.fromEntity(saved);
    }

    public CarteraVencidaDTO calcularPenalidad(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada con ID: " + id));

        if (cartera.getPorcentajePenalidad() != null && cartera.getPorcentajePenalidad().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal penalidad = cartera.getMontoPendiente()
                    .multiply(cartera.getPorcentajePenalidad())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            cartera.setMontoPenalidad(penalidad);
        }

        CarteraVencida saved = carteraVencidaRepository.save(cartera);
        return CarteraVencidaDTO.fromEntity(saved);
    }

    public void deleteCarteraVencida(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada con ID: " + id));
        cartera.setActivo(false);
        carteraVencidaRepository.save(cartera);
    }

    public List<CarteraVencidaDTO> getCarteraByPersona(Long personaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return carteraVencidaRepository.findByEmpresaIdAndPersonaIdAndActivoTrue(empresaId, personaId)
                .stream().map(CarteraVencidaDTO::fromEntity).toList();
    }

    public List<CarteraVencidaDTO> getCarteraByPropiedad(Long propiedadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return carteraVencidaRepository.findByEmpresaIdAndPropiedadIdAndActivoTrue(empresaId, propiedadId)
                .stream().map(CarteraVencidaDTO::fromEntity).toList();
    }

    public List<CarteraVencidaDTO> getCarteraByEstado(String estadoCobranza) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida.EstadoCobranza estado = CarteraVencida.EstadoCobranza.valueOf(estadoCobranza);
        return carteraVencidaRepository.findByEmpresaIdAndEstadoCobranzaAndActivoTrue(empresaId, estado)
                .stream().map(CarteraVencidaDTO::fromEntity).toList();
    }

    public List<CarteraVencidaDTO> getCarteraByClasificacion(String clasificacion) {
        Long empresaId = TenantContext.getCurrentTenant();
        CarteraVencida.ClasificacionAntiguedad clasif = CarteraVencida.ClasificacionAntiguedad.valueOf(clasificacion);
        return carteraVencidaRepository.findByEmpresaIdAndClasificacionAntiguedadAndActivoTrue(empresaId, clasif)
                .stream().map(CarteraVencidaDTO::fromEntity).toList();
    }

    // ========== RESUMEN Y ESTADÍSTICAS ==========

    public ResumenCobranzaDTO getResumenCobranza() {
        Long empresaId = TenantContext.getCurrentTenant();

        BigDecimal totalPendiente = carteraVencidaRepository.sumMontoPendienteByEmpresaId(empresaId);
        BigDecimal totalPenalidad = carteraVencidaRepository.sumMontoPenalidadByEmpresaId(empresaId);
        Long cantidad = carteraVencidaRepository.countByEmpresaIdAndActivoTrue(empresaId);

        if (totalPendiente == null) totalPendiente = BigDecimal.ZERO;
        if (totalPenalidad == null) totalPenalidad = BigDecimal.ZERO;

        return ResumenCobranzaDTO.builder()
                .totalCarteraVencida(totalPendiente)
                .totalPenalidades(totalPenalidad)
                .totalGeneral(totalPendiente.add(totalPenalidad))
                .cantidadCuentasVencidas(cantidad != null ? cantidad.intValue() : 0)

                // Por clasificación
                .montoVigente(getMontoByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VIGENTE))
                .monto1a30(getMontoByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_1_30))
                .monto31a60(getMontoByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_31_60))
                .monto61a90(getMontoByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_61_90))
                .montoMas90(getMontoByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_MAS_90))

                .cantidadVigente(getCantidadByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VIGENTE))
                .cantidad1a30(getCantidadByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_1_30))
                .cantidad31a60(getCantidadByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_31_60))
                .cantidad61a90(getCantidadByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_61_90))
                .cantidadMas90(getCantidadByClasificacion(empresaId, CarteraVencida.ClasificacionAntiguedad.VENCIDO_MAS_90))

                // Por estado
                .pendientes(getCantidadByEstado(empresaId, CarteraVencida.EstadoCobranza.PENDIENTE))
                .enGestion(getCantidadByEstado(empresaId, CarteraVencida.EstadoCobranza.EN_GESTION))
                .promesasPago(getCantidadByEstado(empresaId, CarteraVencida.EstadoCobranza.PROMESA_PAGO))
                .parcialmentePagados(getCantidadByEstado(empresaId, CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO))
                .build();
    }

    private BigDecimal getMontoByClasificacion(Long empresaId, CarteraVencida.ClasificacionAntiguedad clasificacion) {
        BigDecimal monto = carteraVencidaRepository.sumMontoPendienteByClasificacion(empresaId, clasificacion);
        return monto != null ? monto : BigDecimal.ZERO;
    }

    private Integer getCantidadByClasificacion(Long empresaId, CarteraVencida.ClasificacionAntiguedad clasificacion) {
        Long cantidad = carteraVencidaRepository.countByClasificacion(empresaId, clasificacion);
        return cantidad != null ? cantidad.intValue() : 0;
    }

    private Integer getCantidadByEstado(Long empresaId, CarteraVencida.EstadoCobranza estado) {
        Long cantidad = carteraVencidaRepository.countByEstadoCobranza(empresaId, estado);
        return cantidad != null ? cantidad.intValue() : 0;
    }

    // ========== SEGUIMIENTO ==========

    public List<SeguimientoCobranzaDTO> getSeguimientoByCartera(Long carteraVencidaId) {
        return seguimientoCobranzaRepository.findByCarteraVencidaIdAndActivoTrueOrderByFechaContactoDesc(carteraVencidaId)
                .stream().map(SeguimientoCobranzaDTO::fromEntity).toList();
    }

    public SeguimientoCobranzaDTO createSeguimiento(CreateSeguimientoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        // Verificar que existe la cartera vencida
        carteraVencidaRepository.findByIdAndEmpresaId(request.getCarteraVencidaId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cartera vencida no encontrada"));

        SeguimientoCobranza seguimiento = SeguimientoCobranza.builder()
                .empresaId(empresaId)
                .carteraVencidaId(request.getCarteraVencidaId())
                .tipoContacto(SeguimientoCobranza.TipoContacto.valueOf(request.getTipoContacto()))
                .fechaContacto(request.getFechaContacto() != null ? request.getFechaContacto() : java.time.LocalDateTime.now())
                .descripcion(request.getDescripcion())
                .resultado(request.getResultado() != null ? SeguimientoCobranza.ResultadoContacto.valueOf(request.getResultado()) : null)
                .fechaPromesaPago(request.getFechaPromesaPago())
                .montoPromesa(request.getMontoPromesa())
                .proximaAccion(request.getProximaAccion())
                .fechaProximaAccion(request.getFechaProximaAccion())
                .build();

        // Actualizar estado de cartera si hay promesa de pago
        if (request.getFechaPromesaPago() != null) {
            CarteraVencida cartera = carteraVencidaRepository.findByIdAndEmpresaId(request.getCarteraVencidaId(), empresaId).get();
            cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PROMESA_PAGO);
            carteraVencidaRepository.save(cartera);
        }

        SeguimientoCobranza saved = seguimientoCobranzaRepository.save(seguimiento);
        return SeguimientoCobranzaDTO.fromEntity(saved);
    }

    public List<SeguimientoCobranzaDTO> getAccionesPendientes() {
        Long empresaId = TenantContext.getCurrentTenant();
        return seguimientoCobranzaRepository.findByEmpresaIdAndFechaProximaAccionLessThanEqualAndActivoTrue(empresaId, LocalDate.now())
                .stream().map(SeguimientoCobranzaDTO::fromEntity).toList();
    }

    // ========== PROYECCIÓN ==========

    public List<ProyeccionCobranzaDTO> getProyecciones(LocalDate periodoInicio, LocalDate periodoFin) {
        Long empresaId = TenantContext.getCurrentTenant();
        return proyeccionCobranzaRepository.findByEmpresaIdAndPeriodoBetweenAndActivoTrueOrderByPeriodoAsc(empresaId, periodoInicio, periodoFin)
                .stream().map(ProyeccionCobranzaDTO::fromEntity).toList();
    }

    public ProyeccionCobranzaDTO createOrUpdateProyeccion(LocalDate periodo, BigDecimal montoProyectado, Integer cantidadContratos, Integer cantidadPagosEsperados) {
        Long empresaId = TenantContext.getCurrentTenant();

        ProyeccionCobranza proyeccion = proyeccionCobranzaRepository.findByEmpresaIdAndPeriodo(empresaId, periodo)
                .orElse(ProyeccionCobranza.builder()
                        .empresaId(empresaId)
                        .periodo(periodo)
                        .build());

        proyeccion.setMontoProyectado(montoProyectado);
        proyeccion.setCantidadContratos(cantidadContratos);
        proyeccion.setCantidadPagosEsperados(cantidadPagosEsperados);

        ProyeccionCobranza saved = proyeccionCobranzaRepository.save(proyeccion);
        return ProyeccionCobranzaDTO.fromEntity(saved);
    }

    public ProyeccionCobranzaDTO actualizarProyeccionCobrado(LocalDate periodo, BigDecimal montoCobrado, Integer pagosRecibidos) {
        Long empresaId = TenantContext.getCurrentTenant();
        ProyeccionCobranza proyeccion = proyeccionCobranzaRepository.findByEmpresaIdAndPeriodo(empresaId, periodo)
                .orElseThrow(() -> new EntityNotFoundException("Proyección no encontrada para el periodo: " + periodo));

        proyeccion.setMontoCobrado(montoCobrado);
        proyeccion.setCantidadPagosRecibidos(pagosRecibidos);

        ProyeccionCobranza saved = proyeccionCobranzaRepository.save(proyeccion);
        return ProyeccionCobranzaDTO.fromEntity(saved);
    }
}
