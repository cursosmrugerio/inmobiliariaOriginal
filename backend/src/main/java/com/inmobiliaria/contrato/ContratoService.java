package com.inmobiliaria.contrato;

import com.inmobiliaria.contrato.dto.*;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.propiedad.PropiedadRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final PropiedadRepository propiedadRepository;
    private final PersonaRepository personaRepository;

    // --- CRUD Operations ---

    @Transactional(readOnly = true)
    public List<ContratoDTO> getAllContratos(boolean activeOnly, EstadoContrato estado) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Contrato> contratos;

        if (estado != null) {
            contratos = contratoRepository.findByEmpresaIdAndEstado(empresaId, estado);
        } else if (activeOnly) {
            contratos = contratoRepository.findByEmpresaIdAndActivoTrue(empresaId);
        } else {
            contratos = contratoRepository.findByEmpresaId(empresaId);
        }

        return contratos.stream().map(ContratoDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public ContratoDTO getContratoById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional(readOnly = true)
    public List<ContratoDTO> getContratosByPropiedad(Long propiedadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return contratoRepository.findByEmpresaIdAndPropiedadId(empresaId, propiedadId)
                .stream().map(ContratoDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public List<ContratoDTO> getContratosByArrendatario(Long arrendatarioId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return contratoRepository.findByEmpresaIdAndArrendatarioId(empresaId, arrendatarioId)
                .stream().map(ContratoDTO::fromEntityBasic).toList();
    }

    @Transactional
    public ContratoDTO createContrato(CreateContratoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        // Validate dates
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Validate propiedad
        Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(request.getPropiedadId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        // Check if propiedad has active contract
        contratoRepository.findContratoActivoByPropiedad(empresaId, propiedad.getId())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("La propiedad ya tiene un contrato activo");
                });

        // Validate arrendatario
        Persona arrendatario = personaRepository.findByIdAndEmpresaId(request.getArrendatarioId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Arrendatario no encontrado"));

        // Validate aval if provided
        Persona aval = null;
        if (request.getAvalId() != null) {
            aval = personaRepository.findByIdAndEmpresaId(request.getAvalId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Aval no encontrado"));
        }

        // Generate numero contrato if not provided
        String numeroContrato = request.getNumeroContrato();
        if (numeroContrato == null || numeroContrato.isBlank()) {
            numeroContrato = generateNumeroContrato(empresaId);
        } else {
            // Validate uniqueness
            if (contratoRepository.existsByNumeroContratoAndEmpresaId(numeroContrato, empresaId)) {
                throw new IllegalArgumentException("Ya existe un contrato con este número");
            }
        }

        Contrato contrato = Contrato.builder()
                .empresaId(empresaId)
                .numeroContrato(numeroContrato)
                .propiedad(propiedad)
                .arrendatario(arrendatario)
                .aval(aval)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .diaPago(request.getDiaPago())
                .montoRenta(request.getMontoRenta())
                .montoDeposito(request.getMontoDeposito())
                .montoFianza(request.getMontoFianza())
                .montoPenalidadDiaria(request.getMontoPenalidadDiaria())
                .diasGracia(request.getDiasGracia() != null ? request.getDiasGracia() : 0)
                .porcentajeIncrementoAnual(request.getPorcentajeIncrementoAnual())
                .condiciones(request.getCondiciones())
                .notas(request.getNotas())
                .estado(EstadoContrato.BORRADOR)
                .activo(true)
                .build();

        contrato = contratoRepository.save(contrato);
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional
    public ContratoDTO updateContrato(Long id, UpdateContratoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        if (request.getNumeroContrato() != null && !request.getNumeroContrato().equals(contrato.getNumeroContrato())) {
            if (contratoRepository.existsByNumeroContratoAndEmpresaId(request.getNumeroContrato(), empresaId)) {
                throw new IllegalArgumentException("Ya existe un contrato con este número");
            }
            contrato.setNumeroContrato(request.getNumeroContrato());
        }

        if (request.getPropiedadId() != null) {
            Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(request.getPropiedadId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));
            contrato.setPropiedad(propiedad);
        }

        if (request.getArrendatarioId() != null) {
            Persona arrendatario = personaRepository.findByIdAndEmpresaId(request.getArrendatarioId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Arrendatario no encontrado"));
            contrato.setArrendatario(arrendatario);
        }

        if (request.getAvalId() != null) {
            Persona aval = personaRepository.findByIdAndEmpresaId(request.getAvalId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Aval no encontrado"));
            contrato.setAval(aval);
        }

        if (request.getFechaInicio() != null) contrato.setFechaInicio(request.getFechaInicio());
        if (request.getFechaFin() != null) contrato.setFechaFin(request.getFechaFin());
        if (request.getDiaPago() != null) contrato.setDiaPago(request.getDiaPago());
        if (request.getMontoRenta() != null) contrato.setMontoRenta(request.getMontoRenta());
        if (request.getMontoDeposito() != null) contrato.setMontoDeposito(request.getMontoDeposito());
        if (request.getMontoFianza() != null) contrato.setMontoFianza(request.getMontoFianza());
        if (request.getMontoPenalidadDiaria() != null) contrato.setMontoPenalidadDiaria(request.getMontoPenalidadDiaria());
        if (request.getDiasGracia() != null) contrato.setDiasGracia(request.getDiasGracia());
        if (request.getPorcentajeIncrementoAnual() != null) contrato.setPorcentajeIncrementoAnual(request.getPorcentajeIncrementoAnual());
        if (request.getEstado() != null) contrato.setEstado(request.getEstado());
        if (request.getCondiciones() != null) contrato.setCondiciones(request.getCondiciones());
        if (request.getNotas() != null) contrato.setNotas(request.getNotas());
        if (request.getActivo() != null) contrato.setActivo(request.getActivo());

        contrato = contratoRepository.save(contrato);
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional
    public void deleteContrato(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));
        contratoRepository.delete(contrato);
    }

    // --- Contract Lifecycle ---

    @Transactional
    public ContratoDTO activarContrato(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new IllegalStateException("Solo se pueden activar contratos en estado borrador");
        }

        // Check if propiedad already has active contract
        final Long contratoId = contrato.getId();
        contratoRepository.findContratoActivoByPropiedad(empresaId, contrato.getPropiedad().getId())
                .ifPresent(c -> {
                    if (!c.getId().equals(contratoId)) {
                        throw new IllegalArgumentException("La propiedad ya tiene un contrato activo");
                    }
                });

        contrato.setEstado(EstadoContrato.ACTIVO);

        // Mark propiedad as not available
        Propiedad propiedad = contrato.getPropiedad();
        propiedad.setDisponible(false);
        propiedadRepository.save(propiedad);

        contrato = contratoRepository.save(contrato);
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional
    public ContratoDTO terminarContrato(Long id, String motivo) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        if (contrato.getEstado() != EstadoContrato.ACTIVO &&
            contrato.getEstado() != EstadoContrato.POR_VENCER &&
            contrato.getEstado() != EstadoContrato.VENCIDO) {
            throw new IllegalStateException("Solo se pueden terminar contratos activos, por vencer o vencidos");
        }

        contrato.setEstado(EstadoContrato.TERMINADO);
        if (motivo != null) {
            String notasActuales = contrato.getNotas() != null ? contrato.getNotas() + "\n" : "";
            contrato.setNotas(notasActuales + "Terminado: " + motivo);
        }

        // Mark propiedad as available
        Propiedad propiedad = contrato.getPropiedad();
        propiedad.setDisponible(true);
        propiedadRepository.save(propiedad);

        contrato = contratoRepository.save(contrato);
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional
    public ContratoDTO cancelarContrato(Long id, String motivo) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contrato = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        contrato.setEstado(EstadoContrato.CANCELADO);
        if (motivo != null) {
            String notasActuales = contrato.getNotas() != null ? contrato.getNotas() + "\n" : "";
            contrato.setNotas(notasActuales + "Cancelado: " + motivo);
        }

        // Mark propiedad as available if was active
        if (contrato.getEstado() == EstadoContrato.ACTIVO ||
            contrato.getEstado() == EstadoContrato.POR_VENCER) {
            Propiedad propiedad = contrato.getPropiedad();
            propiedad.setDisponible(true);
            propiedadRepository.save(propiedad);
        }

        contrato = contratoRepository.save(contrato);
        return ContratoDTO.fromEntity(contrato);
    }

    @Transactional
    public ContratoDTO renovarContrato(Long id, RenovarContratoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Contrato contratoAnterior = contratoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        if (contratoAnterior.getEstado() != EstadoContrato.ACTIVO &&
            contratoAnterior.getEstado() != EstadoContrato.POR_VENCER &&
            contratoAnterior.getEstado() != EstadoContrato.VENCIDO) {
            throw new IllegalStateException("Solo se pueden renovar contratos activos, por vencer o vencidos");
        }

        // Calculate new rent
        BigDecimal nuevoMontoRenta = request.getNuevoMontoRenta();
        if (nuevoMontoRenta == null) {
            nuevoMontoRenta = contratoAnterior.getMontoRenta();
            if (request.isAplicarIncrementoAnual() && contratoAnterior.getPorcentajeIncrementoAnual() != null) {
                BigDecimal incremento = nuevoMontoRenta
                        .multiply(contratoAnterior.getPorcentajeIncrementoAnual())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                nuevoMontoRenta = nuevoMontoRenta.add(incremento);
            }
        }

        // Validate aval
        Persona nuevoAval = contratoAnterior.getAval();
        if (request.getNuevoAvalId() != null) {
            nuevoAval = personaRepository.findByIdAndEmpresaId(request.getNuevoAvalId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Nuevo aval no encontrado"));
        }

        // Create new contract
        Contrato nuevoContrato = Contrato.builder()
                .empresaId(empresaId)
                .numeroContrato(generateNumeroContrato(empresaId))
                .propiedad(contratoAnterior.getPropiedad())
                .arrendatario(contratoAnterior.getArrendatario())
                .aval(nuevoAval)
                .fechaInicio(contratoAnterior.getFechaFin().plusDays(1))
                .fechaFin(request.getNuevaFechaFin())
                .diaPago(contratoAnterior.getDiaPago())
                .montoRenta(nuevoMontoRenta)
                .montoDeposito(contratoAnterior.getMontoDeposito())
                .montoFianza(contratoAnterior.getMontoFianza())
                .montoPenalidadDiaria(contratoAnterior.getMontoPenalidadDiaria())
                .diasGracia(contratoAnterior.getDiasGracia())
                .porcentajeIncrementoAnual(contratoAnterior.getPorcentajeIncrementoAnual())
                .condiciones(request.getNuevasCondiciones() != null ?
                        request.getNuevasCondiciones() : contratoAnterior.getCondiciones())
                .notas(request.getNotas())
                .contratoAnteriorId(contratoAnterior.getId())
                .estado(EstadoContrato.ACTIVO)
                .activo(true)
                .build();

        // Mark old contract as renewed
        contratoAnterior.setEstado(EstadoContrato.RENOVADO);
        contratoRepository.save(contratoAnterior);

        nuevoContrato = contratoRepository.save(nuevoContrato);
        return ContratoDTO.fromEntity(nuevoContrato);
    }

    // --- Vencimientos ---

    @Transactional(readOnly = true)
    public List<ContratoDTO> getContratosPorVencer(int diasAnticipacion) {
        Long empresaId = TenantContext.getCurrentTenant();
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return contratoRepository.findContratosPorVencer(empresaId, fechaLimite)
                .stream().map(ContratoDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public List<ContratoDTO> getContratosVencidos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return contratoRepository.findContratosVencidos(empresaId, LocalDate.now())
                .stream().map(ContratoDTO::fromEntityBasic).toList();
    }

    @Transactional
    public void actualizarEstadosVencimiento() {
        Long empresaId = TenantContext.getCurrentTenant();
        LocalDate hoy = LocalDate.now();

        // Update contracts to POR_VENCER
        List<Contrato> porVencer = contratoRepository.findContratosPorVencer(empresaId, hoy.plusDays(30));
        for (Contrato contrato : porVencer) {
            if (contrato.getEstado() == EstadoContrato.ACTIVO) {
                contrato.setEstado(EstadoContrato.POR_VENCER);
                contratoRepository.save(contrato);
            }
        }

        // Update contracts to VENCIDO
        List<Contrato> vencidos = contratoRepository.findContratosVencidos(empresaId, hoy);
        for (Contrato contrato : vencidos) {
            if (contrato.getEstado() == EstadoContrato.ACTIVO ||
                contrato.getEstado() == EstadoContrato.POR_VENCER) {
                contrato.setEstado(EstadoContrato.VENCIDO);
                contratoRepository.save(contrato);
            }
        }
    }

    // --- Statistics ---

    @Transactional(readOnly = true)
    public ContratoStats getEstadisticas() {
        Long empresaId = TenantContext.getCurrentTenant();
        return ContratoStats.builder()
                .activos(contratoRepository.countByEmpresaIdAndEstado(empresaId, EstadoContrato.ACTIVO))
                .porVencer(contratoRepository.countByEmpresaIdAndEstado(empresaId, EstadoContrato.POR_VENCER))
                .vencidos(contratoRepository.countByEmpresaIdAndEstado(empresaId, EstadoContrato.VENCIDO))
                .borradores(contratoRepository.countByEmpresaIdAndEstado(empresaId, EstadoContrato.BORRADOR))
                .build();
    }

    // --- Helper Methods ---

    private String generateNumeroContrato(Long empresaId) {
        String prefix = "CTR-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        long count = contratoRepository.findByEmpresaId(empresaId).size() + 1;
        return prefix + String.format("%04d", count);
    }

    @lombok.Data
    @lombok.Builder
    public static class ContratoStats {
        private long activos;
        private long porVencer;
        private long vencidos;
        private long borradores;
    }
}
