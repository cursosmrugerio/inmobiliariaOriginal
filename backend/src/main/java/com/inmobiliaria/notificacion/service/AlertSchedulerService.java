package com.inmobiliaria.notificacion.service;

import com.inmobiliaria.cobranza.dto.CarteraVencidaDTO;
import com.inmobiliaria.cobranza.service.CobranzaService;
import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.contrato.ContratoRepository;
import com.inmobiliaria.notificacion.domain.*;
import com.inmobiliaria.notificacion.dto.CreateNotificacionRequest;
import com.inmobiliaria.notificacion.repository.ConfiguracionNotificacionRepository;
import com.inmobiliaria.notificacion.repository.NotificacionRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertSchedulerService {

    private final NotificacionService notificacionService;
    private final ConfiguracionNotificacionRepository configuracionRepository;
    private final NotificacionRepository notificacionRepository;
    private final CobranzaService cobranzaService;
    private final ContratoRepository contratoRepository;

    @Scheduled(cron = "0 0 8 * * *") // Todos los días a las 8 AM
    public void procesarAlertasDiarias() {
        log.info("Iniciando procesamiento de alertas diarias");

        List<ConfiguracionNotificacion> configuraciones = configuracionRepository.findAll();

        for (ConfiguracionNotificacion config : configuraciones) {
            if (!config.getActivo()) continue;

            try {
                TenantContext.setCurrentTenant(config.getEmpresaId());

                switch (config.getCategoria()) {
                    case VENCIMIENTO_CONTRATO:
                        procesarAlertasVencimientoContrato(config);
                        break;
                    case PAGO_PENDIENTE:
                        procesarRecordatoriosPagoPendiente(config);
                        break;
                    case PAGO_VENCIDO:
                        procesarAlertasPagoVencido(config);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("Error procesando alertas para empresa {}: {}",
                    config.getEmpresaId(), e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }

        log.info("Procesamiento de alertas diarias completado");
    }

    @Transactional
    public void procesarAlertasVencimientoContrato(ConfiguracionNotificacion config) {
        int diasAnticipacion = config.getDiasAnticipacion() != null ? config.getDiasAnticipacion() : 30;
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);

        log.info("Procesando alertas de vencimiento de contrato para empresa {} con {} días de anticipación",
            config.getEmpresaId(), diasAnticipacion);

        try {
            List<Contrato> contratosPorVencer = contratoRepository.findContratosPorVencer(
                config.getEmpresaId(), fechaLimite);

            for (Contrato contrato : contratosPorVencer) {
                crearNotificacionVencimientoContrato(config, contrato);
            }

            log.info("Se procesaron {} contratos próximos a vencer para empresa {}",
                contratosPorVencer.size(), config.getEmpresaId());
        } catch (Exception e) {
            log.error("Error procesando alertas de vencimiento de contrato: {}", e.getMessage());
        }
    }

    private void crearNotificacionVencimientoContrato(ConfiguracionNotificacion config, Contrato contrato) {
        // Verificar frecuencia de recordatorios
        if (!debeEnviarNotificacion(config, contrato.getArrendatario().getId(),
                CategoriaNotificacion.VENCIMIENTO_CONTRATO, contrato.getId())) {
            return;
        }

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), contrato.getFechaFin());
        String mensaje = generarMensajeVencimientoContrato(config, contrato, diasRestantes);
        String asunto = "Aviso: Contrato próximo a vencer - " + contrato.getPropiedad().getDireccionCompleta();

        String emailArrendatario = contrato.getArrendatario().getEmail();
        String telefonoArrendatario = contrato.getArrendatario().getTelefono();

        if (config.getEmailHabilitado() && emailArrendatario != null && !emailArrendatario.isEmpty()) {
            crearNotificacion(
                TipoNotificacion.EMAIL,
                CategoriaNotificacion.VENCIMIENTO_CONTRATO,
                contrato.getArrendatario().getId(),
                emailArrendatario,
                asunto,
                mensaje,
                contrato.getId(),
                "CONTRATO"
            );
        }

        if (config.getWhatsappHabilitado() && telefonoArrendatario != null && !telefonoArrendatario.isEmpty()) {
            crearNotificacion(
                TipoNotificacion.WHATSAPP,
                CategoriaNotificacion.VENCIMIENTO_CONTRATO,
                contrato.getArrendatario().getId(),
                telefonoArrendatario,
                asunto,
                mensaje,
                contrato.getId(),
                "CONTRATO"
            );
        }
    }

    private String generarMensajeVencimientoContrato(ConfiguracionNotificacion config, Contrato contrato, long diasRestantes) {
        if (config.getPlantillaEmail() != null && !config.getPlantillaEmail().isEmpty()) {
            return config.getPlantillaEmail()
                .replace("{{nombre}}", contrato.getArrendatario().getNombreCompleto())
                .replace("{{propiedad}}", contrato.getPropiedad().getDireccionCompleta())
                .replace("{{fecha_vencimiento}}", contrato.getFechaFin().toString())
                .replace("{{dias_restantes}}", String.valueOf(diasRestantes))
                .replace("{{numero_contrato}}", contrato.getNumeroContrato());
        }

        return String.format(
            "Estimado(a) %s,\n\n" +
            "Le informamos que su contrato de arrendamiento número %s " +
            "para la propiedad ubicada en %s vencerá en %d días (fecha: %s).\n\n" +
            "Por favor, comuníquese con nosotros para discutir la renovación o " +
            "terminación del contrato.\n\n" +
            "Saludos cordiales,\n" +
            "Administración",
            contrato.getArrendatario().getNombreCompleto(),
            contrato.getNumeroContrato(),
            contrato.getPropiedad().getDireccionCompleta(),
            diasRestantes,
            contrato.getFechaFin()
        );
    }

    @Transactional
    public void procesarRecordatoriosPagoPendiente(ConfiguracionNotificacion config) {
        log.info("Procesando recordatorios de pago pendiente para empresa {}", config.getEmpresaId());

        try {
            List<CarteraVencidaDTO> cartera = cobranzaService.getAllCarteraVencida(false);

            for (CarteraVencidaDTO item : cartera) {
                if (item.getDiasVencido() <= 0) {
                    // Pago próximo a vencer
                    crearNotificacionPagoPendiente(config, item);
                }
            }
        } catch (Exception e) {
            log.error("Error obteniendo cartera para recordatorios: {}", e.getMessage());
        }
    }

    @Transactional
    public void procesarAlertasPagoVencido(ConfiguracionNotificacion config) {
        log.info("Procesando alertas de pago vencido para empresa {}", config.getEmpresaId());

        try {
            List<CarteraVencidaDTO> cartera = cobranzaService.getAllCarteraVencida(false);

            for (CarteraVencidaDTO item : cartera) {
                if (item.getDiasVencido() > 0) {
                    crearNotificacionPagoVencido(config, item);
                }
            }
        } catch (Exception e) {
            log.error("Error obteniendo cartera vencida: {}", e.getMessage());
        }
    }

    private void crearNotificacionPagoPendiente(ConfiguracionNotificacion config, CarteraVencidaDTO item) {
        // Verificar frecuencia de recordatorios
        if (!debeEnviarNotificacion(config, item.getPersonaId(),
                CategoriaNotificacion.PAGO_PENDIENTE, item.getContratoId())) {
            return;
        }

        String mensaje = generarMensajePagoPendiente(config, item);
        String asunto = "Recordatorio de pago - " + item.getDireccionPropiedad();

        if (config.getEmailHabilitado() && item.getEmailPersona() != null) {
            crearNotificacion(
                TipoNotificacion.EMAIL,
                CategoriaNotificacion.PAGO_PENDIENTE,
                item.getPersonaId(),
                item.getEmailPersona(),
                asunto,
                mensaje,
                item.getContratoId(),
                "CONTRATO"
            );
        }

        if (config.getWhatsappHabilitado() && item.getTelefonoPersona() != null) {
            crearNotificacion(
                TipoNotificacion.WHATSAPP,
                CategoriaNotificacion.PAGO_PENDIENTE,
                item.getPersonaId(),
                item.getTelefonoPersona(),
                asunto,
                mensaje,
                item.getContratoId(),
                "CONTRATO"
            );
        }
    }

    private void crearNotificacionPagoVencido(ConfiguracionNotificacion config, CarteraVencidaDTO item) {
        // Verificar frecuencia de recordatorios
        if (!debeEnviarNotificacion(config, item.getPersonaId(),
                CategoriaNotificacion.PAGO_VENCIDO, item.getContratoId())) {
            return;
        }

        String mensaje = generarMensajePagoVencido(config, item);
        String asunto = "URGENTE: Pago vencido - " + item.getDireccionPropiedad();

        if (config.getEmailHabilitado() && item.getEmailPersona() != null) {
            crearNotificacion(
                TipoNotificacion.EMAIL,
                CategoriaNotificacion.PAGO_VENCIDO,
                item.getPersonaId(),
                item.getEmailPersona(),
                asunto,
                mensaje,
                item.getContratoId(),
                "CONTRATO"
            );
        }

        if (config.getWhatsappHabilitado() && item.getTelefonoPersona() != null) {
            crearNotificacion(
                TipoNotificacion.WHATSAPP,
                CategoriaNotificacion.PAGO_VENCIDO,
                item.getPersonaId(),
                item.getTelefonoPersona(),
                asunto,
                mensaje,
                item.getContratoId(),
                "CONTRATO"
            );
        }
    }

    private void crearNotificacion(TipoNotificacion tipo, CategoriaNotificacion categoria,
                                   Long personaId, String destinatario, String asunto,
                                   String mensaje, Long referenciaId, String referenciaTipo) {
        CreateNotificacionRequest request = CreateNotificacionRequest.builder()
            .personaId(personaId)
            .tipo(tipo)
            .categoria(categoria)
            .destinatario(destinatario)
            .asunto(asunto)
            .mensaje(mensaje)
            .referenciaId(referenciaId)
            .referenciaTipo(referenciaTipo)
            .build();

        notificacionService.create(request);
    }

    private String generarMensajePagoPendiente(ConfiguracionNotificacion config, CarteraVencidaDTO item) {
        if (config.getPlantillaEmail() != null && !config.getPlantillaEmail().isEmpty()) {
            return aplicarPlantilla(config.getPlantillaEmail(), item);
        }

        return String.format(
            "Estimado(a) %s,\n\n" +
            "Le recordamos que tiene un pago pendiente por la cantidad de $%s " +
            "correspondiente a la propiedad ubicada en %s.\n\n" +
            "Por favor, realice su pago a la brevedad posible.\n\n" +
            "Saludos cordiales,\n" +
            "Administración",
            item.getNombrePersona(),
            item.getMontoTotal(),
            item.getDireccionPropiedad()
        );
    }

    private String generarMensajePagoVencido(ConfiguracionNotificacion config, CarteraVencidaDTO item) {
        if (config.getPlantillaEmail() != null && !config.getPlantillaEmail().isEmpty()) {
            return aplicarPlantilla(config.getPlantillaEmail(), item);
        }

        return String.format(
            "Estimado(a) %s,\n\n" +
            "Le informamos que tiene un pago vencido desde hace %d días " +
            "por la cantidad de $%s correspondiente a la propiedad ubicada en %s.\n\n" +
            "Es urgente que regularice su situación para evitar cargos adicionales.\n\n" +
            "Saludos cordiales,\n" +
            "Administración",
            item.getNombrePersona(),
            item.getDiasVencido(),
            item.getMontoTotal(),
            item.getDireccionPropiedad()
        );
    }

    private String aplicarPlantilla(String plantilla, CarteraVencidaDTO item) {
        return plantilla
            .replace("{{nombre}}", item.getNombrePersona() != null ? item.getNombrePersona() : "")
            .replace("{{monto}}", item.getMontoTotal() != null ? item.getMontoTotal().toString() : "0")
            .replace("{{propiedad}}", item.getDireccionPropiedad() != null ? item.getDireccionPropiedad() : "")
            .replace("{{dias_vencido}}", String.valueOf(item.getDiasVencido()));
    }

    /**
     * Verifica si se debe enviar una notificación basado en frecuenciaRecordatorio.
     * @param config Configuración con frecuenciaRecordatorio (en días)
     * @param personaId ID de la persona
     * @param categoria Categoría de notificación
     * @param referenciaId ID de referencia (contratoId)
     * @return true si se debe enviar la notificación
     */
    private boolean debeEnviarNotificacion(ConfiguracionNotificacion config, Long personaId,
                                           CategoriaNotificacion categoria, Long referenciaId) {
        Integer frecuenciaDias = config.getFrecuenciaRecordatorio();

        // Si no hay frecuencia configurada, usar valor por defecto de 7 días
        if (frecuenciaDias == null || frecuenciaDias <= 0) {
            frecuenciaDias = 7;
        }

        var ultimaNotificacion = notificacionRepository.findUltimaNotificacion(
            config.getEmpresaId(), personaId, categoria, referenciaId);

        if (ultimaNotificacion.isEmpty()) {
            // No hay notificación previa, se debe enviar
            return true;
        }

        LocalDateTime fechaUltima = ultimaNotificacion.get().getFechaCreacion();
        LocalDateTime fechaLimite = fechaUltima.plusDays(frecuenciaDias);

        boolean debeEnviar = LocalDateTime.now().isAfter(fechaLimite);

        if (!debeEnviar) {
            log.debug("Notificación omitida para persona {} (última: {}, próxima permitida: {})",
                personaId, fechaUltima, fechaLimite);
        }

        return debeEnviar;
    }

    // Método para procesar notificaciones pendientes
    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void procesarNotificacionesPendientes() {
        log.debug("Procesando notificaciones pendientes de envío");
        notificacionService.procesarPendientes();
    }
}
