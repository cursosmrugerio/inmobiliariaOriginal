package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodigoPostalRepository extends JpaRepository<CodigoPostal, Integer> {

    List<CodigoPostal> findByCodigoAndActivoTrue(String codigo);

    List<CodigoPostal> findByMunicipioIdAndActivoTrue(Integer municipioId);

    List<CodigoPostal> findByActivoTrue();
}
