package com.inmobiliaria.notificacion.service;

import com.inmobiliaria.notificacion.domain.*;
import com.inmobiliaria.notificacion.dto.*;
import com.inmobiliaria.notificacion.repository.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final ConfiguracionNotificacionRepository configuracionRepository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    @Transactional(readOnly = true)
    public List<NotificacionDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return notificacionRepository.findByEmpresaIdOrderByFechaCreacionDesc(empresaId)
                .stream()
                .map(NotificacionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificacionDTO findById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Notificacion notificacion = notificacionRepository.findById(id)
                .filter(n -> n.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));
        return NotificacionDTO.fromEntity(notificacion);
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> findByPersona(Long personaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return notificacionRepository.findByEmpresaIdAndPersonaId(empresaId, personaId)
                .stream()
                .map(NotificacionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> findByEstado(EstadoNotificacion estado) {
        Long empresaId = TenantContext.getCurrentTenant();
        return notificacionRepository.findByEmpresaIdAndEstado(empresaId, estado)
                .stream()
                .map(NotificacionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacionDTO create(CreateNotificacionRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Notificacion notificacion = Notificacion.builder()
                .empresaId(empresaId)
                .personaId(request.getPersonaId())
                .tipo(request.getTipo())
                .categoria(request.getCategoria())
                .estado(EstadoNotificacion.PENDIENTE)
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .referenciaId(request.getReferenciaId())
                .referenciaTipo(request.getReferenciaTipo())
                .fechaProgramada(request.getFechaProgramada())
                .intentos(0)
                .build();

        notificacion = notificacionRepository.save(notificacion);
        log.info("Notificación creada: {} para {}", notificacion.getId(), request.getDestinatario());

        return NotificacionDTO.fromEntity(notificacion);
    }

    @Transactional
    public NotificacionDTO enviar(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Notificacion notificacion = notificacionRepository.findById(id)
                .filter(n -> n.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        return enviarNotificacion(notificacion);
    }

    @Transactional
    public NotificacionDTO enviarNotificacion(Notificacion notificacion) {
        boolean enviado = false;
        String error = null;

        try {
            switch (notificacion.getTipo()) {
                case EMAIL:
                    enviado = emailService.sendEmail(
                            notificacion.getDestinatario(),
                            notificacion.getAsunto(),
                            notificacion.getMensaje()
                    );
                    break;
                case WHATSAPP:
                    enviado = whatsAppService.sendMessage(
                            notificacion.getDestinatario(),
                            notificacion.getMensaje()
                    );
                    break;
                default:
                    error = "Tipo de notificación no soportado";
            }
        } catch (Exception e) {
            error = e.getMessage();
            log.error("Error enviando notificación {}: {}", notificacion.getId(), error);
        }

        notificacion.setIntentos(notificacion.getIntentos() + 1);

        if (enviado) {
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnvio(LocalDateTime.now());
            log.info("Notificación {} enviada exitosamente", notificacion.getId());
        } else {
            notificacion.setEstado(EstadoNotificacion.FALLIDA);
            notificacion.setErrorMensaje(error);
            log.warn("Notificación {} falló: {}", notificacion.getId(), error);
        }

        notificacion = notificacionRepository.save(notificacion);
        return NotificacionDTO.fromEntity(notificacion);
    }

    @Transactional
    public void procesarPendientes() {
        List<Notificacion> pendientes = notificacionRepository
                .findAllPendientesParaEnvio(LocalDateTime.now());

        log.info("Procesando {} notificaciones pendientes", pendientes.size());

        for (Notificacion notificacion : pendientes) {
            try {
                enviarNotificacion(notificacion);
            } catch (Exception e) {
                log.error("Error procesando notificación {}: {}", notificacion.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void cancelar(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Notificacion notificacion = notificacionRepository.findById(id)
                .filter(n -> n.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        notificacion.setEstado(EstadoNotificacion.CANCELADA);
        notificacionRepository.save(notificacion);
        log.info("Notificación {} cancelada", id);
    }

    // Configuración methods
    @Transactional(readOnly = true)
    public List<ConfiguracionNotificacionDTO> findAllConfiguraciones() {
        Long empresaId = TenantContext.getCurrentTenant();
        return configuracionRepository.findByEmpresaId(empresaId)
                .stream()
                .map(ConfiguracionNotificacionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConfiguracionNotificacionDTO updateConfiguracion(UpdateConfiguracionRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        ConfiguracionNotificacion config = configuracionRepository
                .findByEmpresaIdAndCategoria(empresaId, request.getCategoria())
                .orElseGet(() -> ConfiguracionNotificacion.builder()
                        .empresaId(empresaId)
                        .categoria(request.getCategoria())
                        .build());

        if (request.getEmailHabilitado() != null) {
            config.setEmailHabilitado(request.getEmailHabilitado());
        }
        if (request.getWhatsappHabilitado() != null) {
            config.setWhatsappHabilitado(request.getWhatsappHabilitado());
        }
        if (request.getDiasAnticipacion() != null) {
            config.setDiasAnticipacion(request.getDiasAnticipacion());
        }
        if (request.getFrecuenciaRecordatorio() != null) {
            config.setFrecuenciaRecordatorio(request.getFrecuenciaRecordatorio());
        }
        if (request.getMaxIntentos() != null) {
            config.setMaxIntentos(request.getMaxIntentos());
        }
        if (request.getPlantillaEmail() != null) {
            config.setPlantillaEmail(request.getPlantillaEmail());
        }
        if (request.getPlantillaWhatsapp() != null) {
            config.setPlantillaWhatsapp(request.getPlantillaWhatsapp());
        }
        if (request.getActivo() != null) {
            config.setActivo(request.getActivo());
        }

        config = configuracionRepository.save(config);
        log.info("Configuración actualizada para categoría: {}", request.getCategoria());

        return ConfiguracionNotificacionDTO.fromEntity(config);
    }

    @Transactional(readOnly = true)
    public ConfiguracionNotificacionDTO getConfiguracion(CategoriaNotificacion categoria) {
        Long empresaId = TenantContext.getCurrentTenant();
        return configuracionRepository.findByEmpresaIdAndCategoria(empresaId, categoria)
                .map(ConfiguracionNotificacionDTO::fromEntity)
                .orElse(null);
    }
}
