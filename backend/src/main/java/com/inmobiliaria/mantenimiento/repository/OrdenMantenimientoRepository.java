package com.inmobiliaria.mantenimiento.repository;

import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import com.inmobiliaria.mantenimiento.domain.OrdenMantenimiento;
import com.inmobiliaria.mantenimiento.domain.PrioridadOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenMantenimientoRepository extends JpaRepository<OrdenMantenimiento, Long> {

    List<OrdenMantenimiento> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);

    List<OrdenMantenimiento> findByEmpresaIdAndEstadoOrderByPrioridadDescFechaSolicitudAsc(
            Long empresaId, EstadoOrden estado);

    List<OrdenMantenimiento> findByEmpresaIdAndPropiedadIdOrderByFechaCreacionDesc(
            Long empresaId, Long propiedadId);

    List<OrdenMantenimiento> findByEmpresaIdAndProveedorIdOrderByFechaCreacionDesc(
            Long empresaId, Long proveedorId);

    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId " +
           "AND o.estado NOT IN ('COMPLETADA', 'CANCELADA') " +
           "ORDER BY o.prioridad DESC, o.fechaSolicitud ASC")
    List<OrdenMantenimiento> findOrdenesActivas(@Param("empresaId") Long empresaId);

    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId " +
           "AND o.prioridad = :prioridad AND o.estado NOT IN ('COMPLETADA', 'CANCELADA') " +
           "ORDER BY o.fechaSolicitud ASC")
    List<OrdenMantenimiento> findByEmpresaIdAndPrioridad(@Param("empresaId") Long empresaId,
                                                          @Param("prioridad") PrioridadOrden prioridad);

    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId " +
           "AND o.fechaProgramada = :fecha ORDER BY o.prioridad DESC")
    List<OrdenMantenimiento> findProgramadasParaFecha(@Param("empresaId") Long empresaId,
                                                       @Param("fecha") LocalDate fecha);

    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId " +
           "AND o.fechaProgramada BETWEEN :inicio AND :fin ORDER BY o.fechaProgramada, o.prioridad DESC")
    List<OrdenMantenimiento> findProgramadasEnRango(@Param("empresaId") Long empresaId,
                                                     @Param("inicio") LocalDate inicio,
                                                     @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(o) FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId AND o.estado = :estado")
    Long countByEmpresaIdAndEstado(@Param("empresaId") Long empresaId, @Param("estado") EstadoOrden estado);

    @Query("SELECT SUM(o.costoFinal) FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId " +
           "AND o.estado = 'COMPLETADA' AND o.fechaCompletada BETWEEN :inicio AND :fin")
    java.math.BigDecimal sumCostosCompletadosEnRango(@Param("empresaId") Long empresaId,
                                                      @Param("inicio") LocalDate inicio,
                                                      @Param("fin") LocalDate fin);
}
