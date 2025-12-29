package com.inmobiliaria.contrato;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de scheduler para actualizar automáticamente los estados de los contratos.
 *
 * Ejecuta diariamente a las 6:00 AM para:
 * - Marcar contratos como POR_VENCER (30 días antes del vencimiento)
 * - Marcar contratos como VENCIDO (pasada la fecha de fin)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContratoSchedulerService {

    private final ContratoRepository contratoRepository;

    private static final int DIAS_ANTICIPACION_POR_VENCER = 30;

    /**
     * Ejecuta diariamente a las 6:00 AM
     * Cron: segundo minuto hora día-del-mes mes día-de-la-semana
     */
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void actualizarEstadosContratos() {
        log.info("Iniciando actualización automática de estados de contratos");

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimitePorVencer = hoy.plusDays(DIAS_ANTICIPACION_POR_VENCER);

        int contratosVencidos = marcarContratosVencidos(hoy);
        int contratosPorVencer = marcarContratosPorVencer(hoy, fechaLimitePorVencer);

        log.info("Actualización completada: {} contratos marcados como VENCIDO, {} contratos marcados como POR_VENCER",
                contratosVencidos, contratosPorVencer);
    }

    /**
     * Marca contratos activos cuya fecha de fin ya pasó como VENCIDO
     */
    private int marcarContratosVencidos(LocalDate hoy) {
        List<Contrato> contratosActivos = contratoRepository.findAll().stream()
                .filter(c -> c.isActivo())
                .filter(c -> c.getEstado() == EstadoContrato.ACTIVO || c.getEstado() == EstadoContrato.POR_VENCER)
                .filter(c -> c.getFechaFin() != null && c.getFechaFin().isBefore(hoy))
                .toList();

        int contador = 0;
        for (Contrato contrato : contratosActivos) {
            contrato.setEstado(EstadoContrato.VENCIDO);
            contratoRepository.save(contrato);
            contador++;
            log.debug("Contrato {} marcado como VENCIDO (fecha fin: {})",
                    contrato.getNumeroContrato(), contrato.getFechaFin());
        }

        return contador;
    }

    /**
     * Marca contratos activos que vencen en los próximos 30 días como POR_VENCER
     */
    private int marcarContratosPorVencer(LocalDate hoy, LocalDate fechaLimite) {
        List<Contrato> contratosActivos = contratoRepository.findAll().stream()
                .filter(c -> c.isActivo())
                .filter(c -> c.getEstado() == EstadoContrato.ACTIVO)
                .filter(c -> c.getFechaFin() != null)
                .filter(c -> !c.getFechaFin().isBefore(hoy)) // No vencidos aún
                .filter(c -> !c.getFechaFin().isAfter(fechaLimite)) // Dentro de 30 días
                .toList();

        int contador = 0;
        for (Contrato contrato : contratosActivos) {
            contrato.setEstado(EstadoContrato.POR_VENCER);
            contratoRepository.save(contrato);
            contador++;
            log.debug("Contrato {} marcado como POR_VENCER (fecha fin: {})",
                    contrato.getNumeroContrato(), contrato.getFechaFin());
        }

        return contador;
    }

    /**
     * Método para ejecutar manualmente la actualización de estados
     * Útil para testing o ejecución desde un endpoint administrativo
     */
    @Transactional
    public ActualizacionEstadosResult ejecutarActualizacionManual() {
        log.info("Ejecutando actualización manual de estados de contratos");

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimitePorVencer = hoy.plusDays(DIAS_ANTICIPACION_POR_VENCER);

        int contratosVencidos = marcarContratosVencidos(hoy);
        int contratosPorVencer = marcarContratosPorVencer(hoy, fechaLimitePorVencer);

        return new ActualizacionEstadosResult(contratosVencidos, contratosPorVencer);
    }

    public record ActualizacionEstadosResult(int contratosVencidos, int contratosPorVencer) {}
}
