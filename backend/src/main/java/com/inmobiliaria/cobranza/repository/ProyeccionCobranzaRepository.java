package com.inmobiliaria.cobranza.repository;

import com.inmobiliaria.cobranza.domain.ProyeccionCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyeccionCobranzaRepository extends JpaRepository<ProyeccionCobranza, Long> {

    List<ProyeccionCobranza> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<ProyeccionCobranza> findByEmpresaId(Long empresaId);

    Optional<ProyeccionCobranza> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<ProyeccionCobranza> findByEmpresaIdAndPeriodo(Long empresaId, LocalDate periodo);

    List<ProyeccionCobranza> findByEmpresaIdAndPeriodoBetweenAndActivoTrueOrderByPeriodoAsc(
            Long empresaId, LocalDate periodoInicio, LocalDate periodoFin);
}
