package com.inmobiliaria.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByPersonaId(Long personaId);

    List<Direccion> findByPersonaIdAndActivoTrue(Long personaId);

    Optional<Direccion> findByIdAndPersonaId(Long id, Long personaId);

    Optional<Direccion> findByPersonaIdAndEsPrincipalTrue(Long personaId);
}
