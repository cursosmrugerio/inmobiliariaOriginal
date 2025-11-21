package com.inmobiliaria.documento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByEmpresaIdAndActivoTrue(Long empresaId);

    List<Documento> findByEmpresaIdAndTipoDocumentoAndActivoTrue(Long empresaId, TipoDocumento tipoDocumento);

    @Query("SELECT d FROM Documento d WHERE d.empresaId = :empresaId AND d.entidadTipo = :entidadTipo AND d.entidadId = :entidadId AND d.activo = true")
    List<Documento> findByEntidad(@Param("empresaId") Long empresaId,
                                   @Param("entidadTipo") String entidadTipo,
                                   @Param("entidadId") Long entidadId);

    @Query("SELECT COUNT(d) FROM Documento d WHERE d.empresaId = :empresaId AND d.entidadTipo = :entidadTipo AND d.entidadId = :entidadId AND d.activo = true")
    long countByEntidad(@Param("empresaId") Long empresaId,
                        @Param("entidadTipo") String entidadTipo,
                        @Param("entidadId") Long entidadId);
}
