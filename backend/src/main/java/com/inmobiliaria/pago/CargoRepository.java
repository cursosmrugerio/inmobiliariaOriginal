package com.inmobiliaria.pago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

    List<Cargo> findByEmpresaId(Long empresaId);

    Optional<Cargo> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Cargo> findByContratoIdAndEmpresaId(Long contratoId, Long empresaId);

    List<Cargo> findByContratoIdAndEmpresaIdAndEstado(Long contratoId, Long empresaId, EstadoCargo estado);

    @Query("SELECT c FROM Cargo c WHERE c.empresaId = :empresaId AND c.estado IN :estados")
    List<Cargo> findByEmpresaIdAndEstadoIn(@Param("empresaId") Long empresaId,
                                           @Param("estados") List<EstadoCargo> estados);

    @Query("SELECT c FROM Cargo c WHERE c.empresaId = :empresaId " +
           "AND c.fechaVencimiento < :fecha AND c.estado = 'PENDIENTE'")
    List<Cargo> findCargosVencidos(@Param("empresaId") Long empresaId,
                                    @Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM Cargo c WHERE c.empresaId = :empresaId " +
           "AND c.contrato.id = :contratoId AND c.periodoMes = :mes AND c.periodoAnio = :anio " +
           "AND c.tipoCargo = :tipoCargo")
    Optional<Cargo> findCargoByPeriodo(@Param("empresaId") Long empresaId,
                                       @Param("contratoId") Long contratoId,
                                       @Param("mes") Integer mes,
                                       @Param("anio") Integer anio,
                                       @Param("tipoCargo") TipoCargo tipoCargo);

    @Query("SELECT COALESCE(SUM(c.montoPendiente), 0) FROM Cargo c " +
           "WHERE c.empresaId = :empresaId AND c.contrato.id = :contratoId " +
           "AND c.estado IN ('PENDIENTE', 'PARCIAL', 'VENCIDO')")
    java.math.BigDecimal getSaldoPendienteByContrato(@Param("empresaId") Long empresaId,
                                                     @Param("contratoId") Long contratoId);

    @Query("SELECT c FROM Cargo c WHERE c.empresaId = :empresaId " +
           "AND c.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    List<Cargo> findCargosByPeriodo(@Param("empresaId") Long empresaId,
                                     @Param("fechaInicio") LocalDate fechaInicio,
                                     @Param("fechaFin") LocalDate fechaFin);
}
