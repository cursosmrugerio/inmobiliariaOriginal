package com.inmobiliaria.cobranza.repository;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarteraVencidaRepository extends JpaRepository<CarteraVencida, Long> {

    List<CarteraVencida> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<CarteraVencida> findByEmpresaId(Long empresaId);

    Optional<CarteraVencida> findByIdAndEmpresaId(Long id, Long empresaId);

    List<CarteraVencida> findByEmpresaIdAndEstadoCobranzaAndActivoTrue(
            Long empresaId, CarteraVencida.EstadoCobranza estadoCobranza);

    List<CarteraVencida> findByEmpresaIdAndClasificacionAntiguedadAndActivoTrue(
            Long empresaId, CarteraVencida.ClasificacionAntiguedad clasificacion);

    List<CarteraVencida> findByEmpresaIdAndPersonaIdAndActivoTrue(Long empresaId, Long personaId);

    List<CarteraVencida> findByEmpresaIdAndPropiedadIdAndActivoTrue(Long empresaId, Long propiedadId);

    @Query("SELECT SUM(c.montoPendiente) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.activo = true")
    BigDecimal sumMontoPendienteByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT SUM(c.montoPenalidad) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.activo = true")
    BigDecimal sumMontoPenalidadByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(c) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.activo = true")
    Long countByEmpresaIdAndActivoTrue(@Param("empresaId") Long empresaId);

    @Query("SELECT SUM(c.montoPendiente) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.clasificacionAntiguedad = :clasificacion AND c.activo = true")
    BigDecimal sumMontoPendienteByClasificacion(@Param("empresaId") Long empresaId, @Param("clasificacion") CarteraVencida.ClasificacionAntiguedad clasificacion);

    @Query("SELECT COUNT(c) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.clasificacionAntiguedad = :clasificacion AND c.activo = true")
    Long countByClasificacion(@Param("empresaId") Long empresaId, @Param("clasificacion") CarteraVencida.ClasificacionAntiguedad clasificacion);

    @Query("SELECT COUNT(c) FROM CarteraVencida c WHERE c.empresaId = :empresaId AND c.estadoCobranza = :estado AND c.activo = true")
    Long countByEstadoCobranza(@Param("empresaId") Long empresaId, @Param("estado") CarteraVencida.EstadoCobranza estado);

    List<CarteraVencida> findByEmpresaIdAndFechaVencimientoBetweenAndActivoTrue(
            Long empresaId, LocalDate fechaInicio, LocalDate fechaFin);
}
