package com.inmobiliaria.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRolRepository extends JpaRepository<PersonaRol, Long> {

    List<PersonaRol> findByPersonaId(Long personaId);

    List<PersonaRol> findByPersonaIdAndActivoTrue(Long personaId);

    Optional<PersonaRol> findByPersonaIdAndRolId(Long personaId, Integer rolId);

    boolean existsByPersonaIdAndRolId(Long personaId, Integer rolId);

    void deleteByPersonaIdAndRolId(Long personaId, Integer rolId);
}
