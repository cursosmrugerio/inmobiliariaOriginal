package com.inmobiliaria.notificacion.repository;

import com.inmobiliaria.notificacion.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);

    List<Notificacion> findByEmpresaIdAndEstado(Long empresaId, EstadoNotificacion estado);

    List<Notificacion> findByEmpresaIdAndCategoria(Long empresaId, CategoriaNotificacion categoria);

    List<Notificacion> findByEmpresaIdAndPersonaId(Long empresaId, Long personaId);

    @Query("SELECT n FROM Notificacion n WHERE n.empresaId = :empresaId " +
           "AND n.estado = 'PENDIENTE' " +
           "AND (n.fechaProgramada IS NULL OR n.fechaProgramada <= :fecha)")
    List<Notificacion> findPendientesParaEnvio(
            @Param("empresaId") Long empresaId,
            @Param("fecha") LocalDateTime fecha);

    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'PENDIENTE' " +
           "AND (n.fechaProgramada IS NULL OR n.fechaProgramada <= :fecha)")
    List<Notificacion> findAllPendientesParaEnvio(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT n FROM Notificacion n WHERE n.empresaId = :empresaId " +
           "AND n.estado = 'FALLIDA' AND n.intentos < :maxIntentos")
    List<Notificacion> findFallidasParaReintento(
            @Param("empresaId") Long empresaId,
            @Param("maxIntentos") Integer maxIntentos);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.empresaId = :empresaId AND n.estado = :estado")
    Long countByEmpresaIdAndEstado(
            @Param("empresaId") Long empresaId,
            @Param("estado") EstadoNotificacion estado);

    @Query("SELECT n FROM Notificacion n WHERE n.empresaId = :empresaId " +
           "AND n.personaId = :personaId " +
           "AND n.categoria = :categoria " +
           "AND n.referenciaId = :referenciaId " +
           "AND n.estado IN ('ENVIADA', 'PENDIENTE') " +
           "ORDER BY n.fechaCreacion DESC LIMIT 1")
    Optional<Notificacion> findUltimaNotificacion(
            @Param("empresaId") Long empresaId,
            @Param("personaId") Long personaId,
            @Param("categoria") CategoriaNotificacion categoria,
            @Param("referenciaId") Long referenciaId);
}
