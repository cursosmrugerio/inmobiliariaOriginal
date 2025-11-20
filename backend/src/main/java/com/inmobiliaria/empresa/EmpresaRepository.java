package com.inmobiliaria.empresa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    List<Empresa> findByActivoTrue();

    Optional<Empresa> findByIdAndActivoTrue(Long id);

    Optional<Empresa> findByRfc(String rfc);

    boolean existsByRfc(String rfc);
}
