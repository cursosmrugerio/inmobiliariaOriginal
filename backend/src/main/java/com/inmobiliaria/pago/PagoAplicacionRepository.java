package com.inmobiliaria.pago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoAplicacionRepository extends JpaRepository<PagoAplicacion, Long> {

    List<PagoAplicacion> findByPagoId(Long pagoId);

    List<PagoAplicacion> findByCargoId(Long cargoId);

    List<PagoAplicacion> findByEmpresaId(Long empresaId);
}
