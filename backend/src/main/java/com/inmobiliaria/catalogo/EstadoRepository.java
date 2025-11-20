package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    List<Estado> findByActivoTrue();

    Optional<Estado> findByClave(String clave);
}
