package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

    List<Municipio> findByEstadoIdAndActivoTrue(Integer estadoId);

    List<Municipio> findByActivoTrue();
}
