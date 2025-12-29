package com.inmobiliaria.contrato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEmpresaId(Long empresaId);

    List<Contrato> findByEmpresaIdAndActivoTrue(Long empresaId);

    Optional<Contrato> findByIdAndEmpresaId(Long id, Long empresaId);

    boolean existsByNumeroContratoAndEmpresaId(String numeroContrato, Long empresaId);

    List<Contrato> findByEmpresaIdAndEstado(Long empresaId, EstadoContrato estado);

    List<Contrato> findByEmpresaIdAndPropiedadId(Long empresaId, Long propiedadId);

    List<Contrato> findByEmpresaIdAndArrendatarioId(Long empresaId, Long arrendatarioId);

    @Query("SELECT c FROM Contrato c WHERE c.empresaId = :empresaId AND c.estado = 'ACTIVO' " +
           "AND c.fechaFin <= :fechaLimite")
    List<Contrato> findContratosPorVencer(@Param("empresaId") Long empresaId,
                                          @Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT c FROM Contrato c WHERE c.empresaId = :empresaId AND c.estado = 'ACTIVO' " +
           "AND c.fechaFin < :fecha")
    List<Contrato> findContratosVencidos(@Param("empresaId") Long empresaId,
                                         @Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM Contrato c WHERE c.empresaId = :empresaId AND c.propiedad.id = :propiedadId " +
           "AND c.estado IN ('ACTIVO', 'POR_VENCER') AND c.activo = true")
    Optional<Contrato> findContratoActivoByPropiedad(@Param("empresaId") Long empresaId,
                                                      @Param("propiedadId") Long propiedadId);

    @Query("SELECT COUNT(c) FROM Contrato c WHERE c.empresaId = :empresaId AND c.estado = :estado")
    long countByEmpresaIdAndEstado(@Param("empresaId") Long empresaId,
                                   @Param("estado") EstadoContrato estado);
}
