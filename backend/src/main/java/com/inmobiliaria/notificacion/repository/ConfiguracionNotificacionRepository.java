package com.inmobiliaria.notificacion.repository;

import com.inmobiliaria.notificacion.domain.CategoriaNotificacion;
import com.inmobiliaria.notificacion.domain.ConfiguracionNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionNotificacionRepository extends JpaRepository<ConfiguracionNotificacion, Long> {

    List<ConfiguracionNotificacion> findByEmpresaId(Long empresaId);

    Optional<ConfiguracionNotificacion> findByEmpresaIdAndCategoria(Long empresaId, CategoriaNotificacion categoria);

    List<ConfiguracionNotificacion> findByEmpresaIdAndActivo(Long empresaId, Boolean activo);
}
