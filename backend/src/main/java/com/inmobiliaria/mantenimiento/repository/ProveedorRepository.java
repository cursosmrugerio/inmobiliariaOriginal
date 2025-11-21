package com.inmobiliaria.mantenimiento.repository;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByEmpresaIdOrderByNombreAsc(Long empresaId);

    List<Proveedor> findByEmpresaIdAndActivoTrueOrderByNombreAsc(Long empresaId);

    @Query("SELECT p FROM Proveedor p WHERE p.empresaId = :empresaId AND :categoria MEMBER OF p.categorias ORDER BY p.nombre")
    List<Proveedor> findByEmpresaIdAndCategoria(@Param("empresaId") Long empresaId,
                                                 @Param("categoria") CategoriaMantenimiento categoria);

    @Query("SELECT p FROM Proveedor p WHERE p.empresaId = :empresaId AND p.activo = true AND :categoria MEMBER OF p.categorias ORDER BY p.nombre")
    List<Proveedor> findActivosByEmpresaIdAndCategoria(@Param("empresaId") Long empresaId,
                                                        @Param("categoria") CategoriaMantenimiento categoria);

    @Query("SELECT COUNT(p) FROM Proveedor p WHERE p.empresaId = :empresaId AND p.activo = true")
    Long countActivosByEmpresaId(@Param("empresaId") Long empresaId);
}
