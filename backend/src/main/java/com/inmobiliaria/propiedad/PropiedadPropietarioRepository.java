package com.inmobiliaria.propiedad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropiedadPropietarioRepository extends JpaRepository<PropiedadPropietario, Long> {

    List<PropiedadPropietario> findByEmpresaIdAndPropiedadIdAndActivoTrue(Long empresaId, Long propiedadId);

    Optional<PropiedadPropietario> findByEmpresaIdAndPropiedadIdAndPropietarioId(Long empresaId, Long propiedadId, Long propietarioId);

    boolean existsByEmpresaIdAndPropiedadIdAndPropietarioId(Long empresaId, Long propiedadId, Long propietarioId);

    @Modifying
    @Query("DELETE FROM PropiedadPropietario pp WHERE pp.empresaId = :empresaId AND pp.propiedad.id = :propiedadId AND pp.propietario.id = :propietarioId")
    void deleteByEmpresaIdAndPropiedadIdAndPropietarioId(@Param("empresaId") Long empresaId, @Param("propiedadId") Long propiedadId, @Param("propietarioId") Long propietarioId);

    Optional<PropiedadPropietario> findByEmpresaIdAndPropiedadIdAndEsPrincipalTrue(Long empresaId, Long propiedadId);
}
