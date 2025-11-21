package com.inmobiliaria.mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoOrdenRepository extends JpaRepository<SeguimientoOrden, Long> {
    List<SeguimientoOrden> findByOrdenMantenimientoIdOrderByCreatedAtDesc(Long ordenId);
}
