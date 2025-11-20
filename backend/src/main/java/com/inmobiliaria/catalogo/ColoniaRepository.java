package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColoniaRepository extends JpaRepository<Colonia, Integer> {

    List<Colonia> findByMunicipioIdAndActivoTrue(Integer municipioId);

    List<Colonia> findByCodigoPostalAndActivoTrue(String codigoPostal);

    List<Colonia> findByActivoTrue();
}
