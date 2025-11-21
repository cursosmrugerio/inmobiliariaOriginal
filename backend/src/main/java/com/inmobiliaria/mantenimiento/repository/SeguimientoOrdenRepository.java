package com.inmobiliaria.mantenimiento.repository;

import com.inmobiliaria.mantenimiento.domain.SeguimientoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoOrdenRepository extends JpaRepository<SeguimientoOrden, Long> {

    List<SeguimientoOrden> findByOrdenIdOrderByFechaRegistroDesc(Long ordenId);

    void deleteByOrdenId(Long ordenId);
}
