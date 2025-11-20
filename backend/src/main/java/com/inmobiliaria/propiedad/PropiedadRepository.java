package com.inmobiliaria.propiedad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEmpresaId(Long empresaId);

    List<Propiedad> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<Propiedad> findByEmpresaIdAndDisponibleTrue(Long empresaId);

    Optional<Propiedad> findByIdAndEmpresaId(Long id, Long empresaId);

    @Query("SELECT p FROM Propiedad p WHERE p.empresaId = :empresaId AND p.tipoPropiedad.id = :tipoId AND p.activo = true")
    List<Propiedad> findByEmpresaIdAndTipoPropiedad(@Param("empresaId") Long empresaId, @Param("tipoId") Integer tipoId);

    @Query("SELECT p FROM Propiedad p JOIN p.propietarios pp WHERE pp.propietario.id = :propietarioId AND p.activo = true")
    List<Propiedad> findByPropietarioId(@Param("propietarioId") Long propietarioId);

    boolean existsByClaveCatastralAndEmpresaId(String claveCatastral, Long empresaId);
}
