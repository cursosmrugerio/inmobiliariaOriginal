package com.inmobiliaria.propiedad;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropiedadPropietarioRepository extends JpaRepository<PropiedadPropietario, Long> {

    List<PropiedadPropietario> findByPropiedadIdAndActivoTrue(Long propiedadId);

    Optional<PropiedadPropietario> findByPropiedadIdAndPropietarioId(Long propiedadId, Long propietarioId);

    boolean existsByPropiedadIdAndPropietarioId(Long propiedadId, Long propietarioId);

    void deleteByPropiedadIdAndPropietarioId(Long propiedadId, Long propietarioId);

    Optional<PropiedadPropietario> findByPropiedadIdAndEsPrincipalTrue(Long propiedadId);
}
