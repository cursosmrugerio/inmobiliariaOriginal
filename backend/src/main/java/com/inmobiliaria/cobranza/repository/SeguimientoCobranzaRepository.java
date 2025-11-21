package com.inmobiliaria.cobranza.repository;

import com.inmobiliaria.cobranza.domain.SeguimientoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeguimientoCobranzaRepository extends JpaRepository<SeguimientoCobranza, Long> {

    List<SeguimientoCobranza> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<SeguimientoCobranza> findByEmpresaId(Long empresaId);

    Optional<SeguimientoCobranza> findByIdAndEmpresaId(Long id, Long empresaId);

    List<SeguimientoCobranza> findByCarteraVencidaIdAndActivoTrueOrderByFechaContactoDesc(Long carteraVencidaId);

    List<SeguimientoCobranza> findByEmpresaIdAndFechaProximaAccionBetweenAndActivoTrue(
            Long empresaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<SeguimientoCobranza> findByEmpresaIdAndFechaProximaAccionLessThanEqualAndActivoTrue(
            Long empresaId, LocalDate fecha);
}
