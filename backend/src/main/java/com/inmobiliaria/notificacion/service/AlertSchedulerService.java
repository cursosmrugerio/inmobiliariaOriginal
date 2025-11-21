package com.inmobiliaria.notificacion.service;

import com.inmobiliaria.cobranza.dto.CarteraVencidaDTO;
import com.inmobiliaria.cobranza.service.CobranzaService;
import com.inmobiliaria.notificacion.domain.*;
import com.inmobiliaria.notificacion.dto.CreateNotificacionRequest;
import com.inmobiliaria.notificacion.repository.ConfiguracionNotificacionRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertSchedulerService {

    private final NotificacionService notificacionService;
    private final ConfiguracionNotificacionRepository configuracionRepository;
    private final CobranzaService cobranzaService;

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

        // TODO: Integrar con ContratosService cuando esté implementado
        // Por ahora, este método está preparado para recibir contratos próximos a vencer
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

    // Método para procesar notificaciones pendientes
    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void procesarNotificacionesPendientes() {
        log.debug("Procesando notificaciones pendientes de envío");
        notificacionService.procesarPendientes();
    }
}
