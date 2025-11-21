package com.inmobiliaria.pago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByEmpresaId(Long empresaId);

    Optional<Pago> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Pago> findByContratoIdAndEmpresaId(Long contratoId, Long empresaId);

    List<Pago> findByPersonaIdAndEmpresaId(Long personaId, Long empresaId);

    @Query("SELECT p FROM Pago p WHERE p.empresaId = :empresaId AND p.estado = :estado")
    List<Pago> findByEmpresaIdAndEstado(@Param("empresaId") Long empresaId,
                                         @Param("estado") EstadoPago estado);

    @Query("SELECT p FROM Pago p WHERE p.empresaId = :empresaId " +
           "AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<Pago> findPagosByPeriodo(@Param("empresaId") Long empresaId,
                                   @Param("fechaInicio") LocalDate fechaInicio,
                                   @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.empresaId = :empresaId AND p.estado = 'APLICADO' " +
           "AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal getTotalPagadoByPeriodo(@Param("empresaId") Long empresaId,
                                        @Param("fechaInicio") LocalDate fechaInicio,
                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Pago p WHERE p.empresaId = :empresaId " +
           "AND p.contrato.id = :contratoId ORDER BY p.fechaPago DESC")
    List<Pago> findPagosByContratoOrdenados(@Param("empresaId") Long empresaId,
                                             @Param("contratoId") Long contratoId);

    @Query("SELECT MAX(p.numeroRecibo) FROM Pago p WHERE p.empresaId = :empresaId")
    String findUltimoNumeroRecibo(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.empresaId = :empresaId " +
           "AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    Long countPagosByPeriodo(@Param("empresaId") Long empresaId,
                              @Param("fechaInicio") LocalDate fechaInicio,
                              @Param("fechaFin") LocalDate fechaFin);
}
