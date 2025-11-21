package com.inmobiliaria.mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenMantenimientoRepository extends JpaRepository<OrdenMantenimiento, Long> {
    List<OrdenMantenimiento> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId);
    
    List<OrdenMantenimiento> findByEmpresaIdAndEstadoOrderByCreatedAtDesc(Long empresaId, EstadoOrden estado);
    
    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId AND o.propiedad.id = :propiedadId ORDER BY o.createdAt DESC")
    List<OrdenMantenimiento> findByPropiedadId(@Param("empresaId") Long empresaId, @Param("propiedadId") Long propiedadId);
    
    @Query("SELECT o FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId AND o.proveedor.id = :proveedorId ORDER BY o.createdAt DESC")
    List<OrdenMantenimiento> findByProveedorId(@Param("empresaId") Long empresaId, @Param("proveedorId") Long proveedorId);
    
    @Query("SELECT COUNT(o) FROM OrdenMantenimiento o WHERE o.empresaId = :empresaId AND o.estado = :estado")
    long countByEstado(@Param("empresaId") Long empresaId, @Param("estado") EstadoOrden estado);
}
