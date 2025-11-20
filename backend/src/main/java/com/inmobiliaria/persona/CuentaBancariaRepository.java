package com.inmobiliaria.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {

    List<CuentaBancaria> findByPersonaId(Long personaId);

    List<CuentaBancaria> findByPersonaIdAndActivoTrue(Long personaId);

    Optional<CuentaBancaria> findByIdAndPersonaId(Long id, Long personaId);

    Optional<CuentaBancaria> findByPersonaIdAndEsPrincipalTrue(Long personaId);

    boolean existsByClabe(String clabe);
}
