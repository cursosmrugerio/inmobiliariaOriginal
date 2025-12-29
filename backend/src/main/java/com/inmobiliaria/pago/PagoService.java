package com.inmobiliaria.pago;

import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.contrato.ContratoRepository;
import com.inmobiliaria.contrato.EstadoContrato;
import com.inmobiliaria.pago.dto.*;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CargoRepository cargoRepository;
    private final PagoAplicacionRepository pagoAplicacionRepository;
    private final ContratoRepository contratoRepository;
    private final PersonaRepository personaRepository;

    // ==================== PAGOS ====================

    public List<PagoDTO> getAllPagos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return pagoRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PagoDTO getPagoById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Pago pago = pagoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));
        return toDTO(pago);
    }

    public Pago getPagoEntity(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        return pagoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));
    }

    public List<PagoDTO> getPagosByContrato(Long contratoId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return pagoRepository.findPagosByContratoOrdenados(empresaId, contratoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> getPagosByPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        Long empresaId = TenantContext.getCurrentTenant();
        return pagoRepository.findPagosByPeriodo(empresaId, fechaInicio, fechaFin).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PagoDTO createPago(CreatePagoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Contrato contrato = contratoRepository.findByIdAndEmpresaId(request.getContratoId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        Persona persona = personaRepository.findByIdAndEmpresaId(request.getPersonaId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        String numeroRecibo = generarNumeroRecibo(empresaId);

        Pago pago = Pago.builder()
                .empresaId(empresaId)
                .contrato(contrato)
                .persona(persona)
                .numeroRecibo(numeroRecibo)
                .monto(request.getMonto())
                .tipoPago(request.getTipoPago())
                .fechaPago(request.getFechaPago())
                .referencia(request.getReferencia())
                .banco(request.getBanco())
                .numeroCheque(request.getNumeroCheque())
                .notas(request.getNotas())
                .comprobanteUrl(request.getComprobanteUrl())
                .estado(EstadoPago.PENDIENTE)
                .build();

        pago = pagoRepository.save(pago);

        if (Boolean.TRUE.equals(request.getAplicarAutomaticamente())) {
            aplicarPagoAutomatico(pago);
        } else if (request.getCargoIds() != null && !request.getCargoIds().isEmpty()) {
            aplicarPagoACargos(pago, request.getCargoIds());
        }

        return toDTO(pago);
    }

    public PagoDTO aplicarPago(Long pagoId, AplicarPagoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Pago pago = pagoRepository.findByIdAndEmpresaId(pagoId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        Cargo cargo = cargoRepository.findByIdAndEmpresaId(request.getCargoId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));

        BigDecimal montoDisponible = pago.getMontoDisponible();
        if (request.getMontoAplicar().compareTo(montoDisponible) > 0) {
            throw new IllegalArgumentException("El monto a aplicar excede el monto disponible del pago");
        }

        if (request.getMontoAplicar().compareTo(cargo.getMontoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto a aplicar excede el monto pendiente del cargo");
        }

        aplicarMontoACargo(pago, cargo, request.getMontoAplicar());

        return toDTO(pago);
    }

    public void cancelarPago(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Pago pago = pagoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        if (pago.getEstado() == EstadoPago.APLICADO || pago.getEstado() == EstadoPago.PARCIAL) {
            revertirAplicaciones(pago);
        }

        pago.setEstado(EstadoPago.CANCELADO);
        pagoRepository.save(pago);
    }

    private void aplicarPagoAutomatico(Pago pago) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Cargo> cargosPendientes = cargoRepository
                .findByContratoIdAndEmpresaIdAndEstado(pago.getContrato().getId(), empresaId, EstadoCargo.PENDIENTE);

        cargosPendientes.addAll(cargoRepository
                .findByContratoIdAndEmpresaIdAndEstado(pago.getContrato().getId(), empresaId, EstadoCargo.PARCIAL));

        cargosPendientes.sort(Comparator.comparing(Cargo::getFechaVencimiento));

        BigDecimal montoRestante = pago.getMonto();
        for (Cargo cargo : cargosPendientes) {
            if (montoRestante.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal montoAAplicar = montoRestante.min(cargo.getMontoPendiente());
            aplicarMontoACargo(pago, cargo, montoAAplicar);
            montoRestante = montoRestante.subtract(montoAAplicar);
        }
    }

    private void aplicarPagoACargos(Pago pago, List<Long> cargoIds) {
        Long empresaId = TenantContext.getCurrentTenant();
        BigDecimal montoRestante = pago.getMonto();

        for (Long cargoId : cargoIds) {
            if (montoRestante.compareTo(BigDecimal.ZERO) <= 0) break;

            Cargo cargo = cargoRepository.findByIdAndEmpresaId(cargoId, empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado: " + cargoId));

            BigDecimal montoAAplicar = montoRestante.min(cargo.getMontoPendiente());
            aplicarMontoACargo(pago, cargo, montoAAplicar);
            montoRestante = montoRestante.subtract(montoAAplicar);
        }
    }

    private void aplicarMontoACargo(Pago pago, Cargo cargo, BigDecimal monto) {
        Long empresaId = TenantContext.getCurrentTenant();

        pago.aplicarMonto(monto);
        cargo.aplicarPago(monto);

        PagoAplicacion aplicacion = PagoAplicacion.builder()
                .empresaId(empresaId)
                .pago(pago)
                .cargo(cargo)
                .montoAplicado(monto)
                .build();

        pagoAplicacionRepository.save(aplicacion);
        cargoRepository.save(cargo);
        pagoRepository.save(pago);

        if (pago.getMontoDisponible().compareTo(BigDecimal.ZERO) <= 0) {
            pago.setFechaAplicacion(LocalDate.now());
        }
    }

    private void revertirAplicaciones(Pago pago) {
        List<PagoAplicacion> aplicaciones = pagoAplicacionRepository.findByPagoId(pago.getId());

        for (PagoAplicacion aplicacion : aplicaciones) {
            Cargo cargo = aplicacion.getCargo();
            cargo.setMontoPagado(cargo.getMontoPagado().subtract(aplicacion.getMontoAplicado()));
            cargo.setMontoPendiente(cargo.getMontoOriginal().subtract(cargo.getMontoPagado()));

            if (cargo.getMontoPendiente().compareTo(cargo.getMontoOriginal()) >= 0) {
                cargo.setEstado(EstadoCargo.PENDIENTE);
            } else {
                cargo.setEstado(EstadoCargo.PARCIAL);
            }

            cargoRepository.save(cargo);
            pagoAplicacionRepository.delete(aplicacion);
        }

        pago.setMontoAplicado(BigDecimal.ZERO);
        pago.setFechaAplicacion(null);
    }

    private String generarNumeroRecibo(Long empresaId) {
        String ultimo = pagoRepository.findUltimoNumeroRecibo(empresaId);
        int siguiente = 1;
        if (ultimo != null && ultimo.startsWith("REC-")) {
            try {
                siguiente = Integer.parseInt(ultimo.substring(4)) + 1;
            } catch (NumberFormatException e) {
                // Usar valor por defecto
            }
        }
        return String.format("REC-%06d", siguiente);
    }

    // ==================== CARGOS ====================

    public List<CargoDTO> getAllCargos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return cargoRepository.findByEmpresaId(empresaId).stream()
                .map(this::toCargoDTO)
                .collect(Collectors.toList());
    }

    public CargoDTO getCargoById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Cargo cargo = cargoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));
        return toCargoDTO(cargo);
    }

    public List<CargoDTO> getCargosByContrato(Long contratoId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return cargoRepository.findByContratoIdAndEmpresaId(contratoId, empresaId).stream()
                .map(this::toCargoDTO)
                .collect(Collectors.toList());
    }

    public List<CargoDTO> getCargosPendientes() {
        Long empresaId = TenantContext.getCurrentTenant();
        List<EstadoCargo> estados = List.of(EstadoCargo.PENDIENTE, EstadoCargo.PARCIAL, EstadoCargo.VENCIDO);
        return cargoRepository.findByEmpresaIdAndEstadoIn(empresaId, estados).stream()
                .map(this::toCargoDTO)
                .collect(Collectors.toList());
    }

    public List<CargoDTO> getCargosVencidos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return cargoRepository.findCargosVencidos(empresaId, LocalDate.now()).stream()
                .map(this::toCargoDTO)
                .collect(Collectors.toList());
    }

    public CargoDTO createCargo(CreateCargoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Contrato contrato = contratoRepository.findByIdAndEmpresaId(request.getContratoId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        Cargo cargo = Cargo.builder()
                .empresaId(empresaId)
                .contrato(contrato)
                .tipoCargo(request.getTipoCargo())
                .concepto(request.getConcepto())
                .montoOriginal(request.getMontoOriginal())
                .montoPendiente(request.getMontoOriginal())
                .fechaCargo(request.getFechaCargo())
                .fechaVencimiento(request.getFechaVencimiento())
                .esCargoFijo(request.getEsCargoFijo())
                .periodoMes(request.getPeriodoMes())
                .periodoAnio(request.getPeriodoAnio())
                .notas(request.getNotas())
                .estado(EstadoCargo.PENDIENTE)
                .build();

        cargo = cargoRepository.save(cargo);
        return toCargoDTO(cargo);
    }

    public void cancelarCargo(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Cargo cargo = cargoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));

        if (cargo.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No se puede cancelar un cargo con pagos aplicados");
        }

        cargo.setEstado(EstadoCargo.CANCELADO);
        cargoRepository.save(cargo);
    }

    // ==================== GENERACIÓN AUTOMÁTICA ====================

    public List<CargoDTO> generarCargosFijos(GenerarCargosFijosRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Cargo> cargosGenerados = new ArrayList<>();

        List<Contrato> contratos;
        if (request.getContratoId() != null) {
            contratos = contratoRepository.findByIdAndEmpresaId(request.getContratoId(), empresaId)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            contratos = contratoRepository.findByEmpresaIdAndEstado(empresaId, EstadoContrato.ACTIVO);
        }

        YearMonth periodo = YearMonth.of(request.getAnio(), request.getMes());
        LocalDate fechaCargo = periodo.atDay(1);

        for (Contrato contrato : contratos) {
            // Verificar si ya existe cargo de renta para este periodo
            if (cargoRepository.findCargoByPeriodo(empresaId, contrato.getId(),
                    request.getMes(), request.getAnio(), TipoCargo.RENTA).isEmpty()) {

                int diaPago = contrato.getDiaPago() != null ? contrato.getDiaPago() : 1;
                int ultimoDia = periodo.lengthOfMonth();
                LocalDate fechaVencimiento = periodo.atDay(Math.min(diaPago, ultimoDia));

                Cargo cargo = Cargo.builder()
                        .empresaId(empresaId)
                        .contrato(contrato)
                        .tipoCargo(TipoCargo.RENTA)
                        .concepto("Renta " + periodo.getMonth().name() + " " + request.getAnio())
                        .montoOriginal(contrato.getMontoRenta())
                        .montoPendiente(contrato.getMontoRenta())
                        .fechaCargo(fechaCargo)
                        .fechaVencimiento(fechaVencimiento)
                        .esCargoFijo(true)
                        .periodoMes(request.getMes())
                        .periodoAnio(request.getAnio())
                        .estado(EstadoCargo.PENDIENTE)
                        .build();

                cargosGenerados.add(cargoRepository.save(cargo));
            }
        }

        return cargosGenerados.stream()
                .map(this::toCargoDTO)
                .collect(Collectors.toList());
    }

    public void actualizarCargosVencidos() {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Cargo> cargosVencidos = cargoRepository.findCargosVencidos(empresaId, LocalDate.now());

        for (Cargo cargo : cargosVencidos) {
            cargo.setEstado(EstadoCargo.VENCIDO);
            cargoRepository.save(cargo);
        }
    }

    // ==================== ESTADÍSTICAS ====================

    public PagoEstadisticas getEstadisticas() {
        Long empresaId = TenantContext.getCurrentTenant();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        BigDecimal totalPagadoMes = pagoRepository.getTotalPagadoByPeriodo(empresaId, inicioMes, finMes);
        Long totalPagosMes = pagoRepository.countPagosByPeriodo(empresaId, inicioMes, finMes);

        List<EstadoCargo> estadosPendientes = List.of(EstadoCargo.PENDIENTE, EstadoCargo.PARCIAL, EstadoCargo.VENCIDO);
        List<Cargo> cargosPendientes = cargoRepository.findByEmpresaIdAndEstadoIn(empresaId, estadosPendientes);

        BigDecimal totalPendiente = cargosPendientes.stream()
                .map(Cargo::getMontoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cargosVencidos = cargosPendientes.stream()
                .filter(c -> c.getEstado() == EstadoCargo.VENCIDO)
                .count();

        return PagoEstadisticas.builder()
                .totalPagadoMes(totalPagadoMes)
                .totalPagosMes(totalPagosMes)
                .totalPendiente(totalPendiente)
                .totalCargosPendientes((long) cargosPendientes.size())
                .totalCargosVencidos(cargosVencidos)
                .build();
    }

    public BigDecimal getSaldoPendienteContrato(Long contratoId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return cargoRepository.getSaldoPendienteByContrato(empresaId, contratoId);
    }

    // ==================== MAPPERS ====================

    private PagoDTO toDTO(Pago pago) {
        List<PagoAplicacion> aplicaciones = pagoAplicacionRepository.findByPagoId(pago.getId());

        return PagoDTO.builder()
                .id(pago.getId())
                .contratoId(pago.getContrato().getId())
                .numeroContrato(pago.getContrato().getNumeroContrato())
                .personaId(pago.getPersona().getId())
                .personaNombre(pago.getPersona().getNombreCompleto())
                .propiedadDireccion(pago.getContrato().getPropiedad().getDireccionCompleta())
                .numeroRecibo(pago.getNumeroRecibo())
                .monto(pago.getMonto())
                .montoAplicado(pago.getMontoAplicado())
                .montoDisponible(pago.getMontoDisponible())
                .tipoPago(pago.getTipoPago())
                .estado(pago.getEstado())
                .fechaPago(pago.getFechaPago())
                .fechaAplicacion(pago.getFechaAplicacion())
                .referencia(pago.getReferencia())
                .banco(pago.getBanco())
                .numeroCheque(pago.getNumeroCheque())
                .notas(pago.getNotas())
                .comprobanteUrl(pago.getComprobanteUrl())
                .aplicaciones(aplicaciones.stream().map(this::toAplicacionDTO).collect(Collectors.toList()))
                .createdAt(pago.getCreatedAt())
                .build();
    }

    private CargoDTO toCargoDTO(Cargo cargo) {
        return CargoDTO.builder()
                .id(cargo.getId())
                .contratoId(cargo.getContrato().getId())
                .numeroContrato(cargo.getContrato().getNumeroContrato())
                .propiedadDireccion(cargo.getContrato().getPropiedad().getDireccionCompleta())
                .arrendatarioNombre(cargo.getContrato().getArrendatario().getNombreCompleto())
                .tipoCargo(cargo.getTipoCargo())
                .concepto(cargo.getConcepto())
                .montoOriginal(cargo.getMontoOriginal())
                .montoPagado(cargo.getMontoPagado())
                .montoPendiente(cargo.getMontoPendiente())
                .fechaCargo(cargo.getFechaCargo())
                .fechaVencimiento(cargo.getFechaVencimiento())
                .estado(cargo.getEstado())
                .esCargoFijo(cargo.getEsCargoFijo())
                .periodoMes(cargo.getPeriodoMes())
                .periodoAnio(cargo.getPeriodoAnio())
                .notas(cargo.getNotas())
                .createdAt(cargo.getCreatedAt())
                .build();
    }

    private PagoAplicacionDTO toAplicacionDTO(PagoAplicacion aplicacion) {
        return PagoAplicacionDTO.builder()
                .id(aplicacion.getId())
                .pagoId(aplicacion.getPago().getId())
                .cargoId(aplicacion.getCargo().getId())
                .cargoConcepto(aplicacion.getCargo().getConcepto())
                .montoAplicado(aplicacion.getMontoAplicado())
                .createdAt(aplicacion.getCreatedAt())
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class PagoEstadisticas {
        private BigDecimal totalPagadoMes;
        private Long totalPagosMes;
        private BigDecimal totalPendiente;
        private Long totalCargosPendientes;
        private Long totalCargosVencidos;
    }
}
