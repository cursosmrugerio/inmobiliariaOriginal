package com.inmobiliaria.cobranza.service;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import com.inmobiliaria.cobranza.repository.CarteraVencidaRepository;
import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.empresa.Empresa;
import com.inmobiliaria.empresa.EmpresaRepository;
import com.inmobiliaria.pago.Cargo;
import com.inmobiliaria.pago.CargoRepository;
import com.inmobiliaria.pago.EstadoCargo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que sincroniza automáticamente los cargos vencidos del módulo de pagos
 * con la cartera de cobranza, eliminando la necesidad de crear cartera manualmente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MorosidadAutomaticaService {

    private final CargoRepository cargoRepository;
    private final CarteraVencidaRepository carteraVencidaRepository;
    private final EmpresaRepository empresaRepository;

    /**
     * Proceso scheduled que sincroniza cargos vencidos con cartera de cobranza.
     * Se ejecuta todos los días a las 6 AM.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void sincronizarMorosidadAutomatica() {
        log.info("Iniciando sincronización automática de morosidad");

        List<Empresa> empresas = empresaRepository.findByActivoTrue();

        for (Empresa empresa : empresas) {
            try {
                sincronizarMorosidadPorEmpresa(empresa.getId());
            } catch (Exception e) {
                log.error("Error sincronizando morosidad para empresa {}: {}", empresa.getId(), e.getMessage());
            }
        }

        log.info("Sincronización automática de morosidad completada");
    }

    /**
     * Sincroniza la morosidad para una empresa específica.
     * Puede ser llamado manualmente o por el scheduler.
     */
    @Transactional
    public SincronizacionResult sincronizarMorosidadPorEmpresa(Long empresaId) {
        log.info("Sincronizando morosidad para empresa {}", empresaId);

        LocalDate hoy = LocalDate.now();

        // Obtener cargos vencidos (fecha vencimiento < hoy y no pagados)
        List<Cargo> cargosVencidos = cargoRepository.findCargosVencidos(empresaId, hoy);

        int nuevosRegistros = 0;
        int actualizados = 0;
        int sinCambios = 0;

        for (Cargo cargo : cargosVencidos) {
            // Verificar si el cargo ya está en cartera
            Optional<CarteraVencida> carteraExistente = carteraVencidaRepository.findByCargoIdentifier(
                    empresaId,
                    cargo.getContrato().getId(),
                    cargo.getConcepto(),
                    cargo.getFechaVencimiento()
            );

            if (carteraExistente.isPresent()) {
                // Actualizar monto pendiente si cambió
                CarteraVencida cartera = carteraExistente.get();
                if (!cartera.getMontoPendiente().equals(cargo.getMontoPendiente())) {
                    cartera.setMontoPendiente(cargo.getMontoPendiente());

                    // Actualizar estado si se pagó parcialmente
                    if (cargo.getEstado() == EstadoCargo.PARCIAL) {
                        cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO);
                    } else if (cargo.getEstado() == EstadoCargo.PAGADO) {
                        cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PAGADO);
                        cartera.setActivo(false);
                    }

                    carteraVencidaRepository.save(cartera);
                    actualizados++;
                } else {
                    sinCambios++;
                }
            } else {
                // Crear nuevo registro en cartera
                crearCarteraDesdeCargoVencido(cargo);
                nuevosRegistros++;
            }
        }

        // Desactivar registros de cartera cuyos cargos ya fueron pagados
        List<CarteraVencida> carteraActiva = carteraVencidaRepository.findByEmpresaIdAndActivoTrue(empresaId);
        int desactivados = 0;

        for (CarteraVencida cartera : carteraActiva) {
            // Buscar si el cargo correspondiente ya está pagado
            List<Cargo> cargosContrato = cargoRepository.findByContratoIdAndEmpresaId(
                    cartera.getContratoId(), empresaId);

            boolean cargoPagado = cargosContrato.stream()
                    .filter(c -> c.getConcepto().equals(cartera.getConcepto())
                            && c.getFechaVencimiento().equals(cartera.getFechaVencimiento()))
                    .anyMatch(c -> c.getEstado() == EstadoCargo.PAGADO);

            if (cargoPagado && cartera.getActivo()) {
                cartera.setEstadoCobranza(CarteraVencida.EstadoCobranza.PAGADO);
                cartera.setActivo(false);
                cartera.setMontoPendiente(BigDecimal.ZERO);
                carteraVencidaRepository.save(cartera);
                desactivados++;
            }
        }

        log.info("Sincronización empresa {}: {} nuevos, {} actualizados, {} sin cambios, {} desactivados",
                empresaId, nuevosRegistros, actualizados, sinCambios, desactivados);

        return new SincronizacionResult(nuevosRegistros, actualizados, sinCambios, desactivados);
    }

    private void crearCarteraDesdeCargoVencido(Cargo cargo) {
        Contrato contrato = cargo.getContrato();

        CarteraVencida cartera = CarteraVencida.builder()
                .empresaId(cargo.getEmpresaId())
                .contratoId(contrato.getId())
                .personaId(contrato.getArrendatario().getId())
                .propiedadId(contrato.getPropiedad().getId())
                .montoOriginal(cargo.getMontoOriginal())
                .montoPendiente(cargo.getMontoPendiente())
                .fechaVencimiento(cargo.getFechaVencimiento())
                .concepto(cargo.getConcepto())
                .estadoCobranza(cargo.getEstado() == EstadoCargo.PARCIAL
                        ? CarteraVencida.EstadoCobranza.PARCIALMENTE_PAGADO
                        : CarteraVencida.EstadoCobranza.PENDIENTE)
                .activo(true)
                .build();

        // Calcular penalidad si aplica
        if (contrato.getMontoPenalidadDiaria() != null && contrato.getMontoPenalidadDiaria().compareTo(BigDecimal.ZERO) > 0) {
            long diasVencido = java.time.temporal.ChronoUnit.DAYS.between(cargo.getFechaVencimiento(), LocalDate.now());
            int diasGracia = contrato.getDiasGracia() != null ? contrato.getDiasGracia() : 0;

            if (diasVencido > diasGracia) {
                BigDecimal penalidad = contrato.getMontoPenalidadDiaria()
                        .multiply(BigDecimal.valueOf(diasVencido - diasGracia));
                cartera.setMontoPenalidad(penalidad);
            }
        }

        carteraVencidaRepository.save(cartera);
    }

    /**
     * Resultado de la sincronización para reportes.
     */
    public record SincronizacionResult(
            int nuevosRegistros,
            int actualizados,
            int sinCambios,
            int desactivados
    ) {}
}
