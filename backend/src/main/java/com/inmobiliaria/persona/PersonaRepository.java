package com.inmobiliaria.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    List<Persona> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<Persona> findByEmpresaId(Long empresaId);

    Optional<Persona> findByIdAndEmpresaId(Long id, Long empresaId);

    Optional<Persona> findByIdAndEmpresaIdAndActivoTrue(Long id, Long empresaId);

    @Query("SELECT p FROM Persona p JOIN p.roles pr WHERE p.empresaId = :empresaId AND pr.rol.id = :rolId AND p.activo = true AND pr.activo = true")
    List<Persona> findByEmpresaIdAndRol(@Param("empresaId") Long empresaId, @Param("rolId") Integer rolId);

    boolean existsByRfcAndEmpresaId(String rfc, Long empresaId);

    boolean existsByEmailAndEmpresaId(String email, Long empresaId);

    Optional<Persona> findByRfcAndEmpresaId(String rfc, Long empresaId);
}
