package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoPropiedadRepository extends JpaRepository<TipoPropiedad, Integer> {
    List<TipoPropiedad> findByActivoTrue();
}
