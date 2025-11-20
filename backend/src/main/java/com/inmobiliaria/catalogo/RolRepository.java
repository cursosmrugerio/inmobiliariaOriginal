package com.inmobiliaria.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findByActivoTrue();

    Optional<Rol> findByClave(String clave);
}
