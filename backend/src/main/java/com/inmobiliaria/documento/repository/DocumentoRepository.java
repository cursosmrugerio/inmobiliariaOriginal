package com.inmobiliaria.documento.repository;

import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);

    List<Documento> findByEmpresaIdAndTipoEntidadAndEntidadIdOrderByFechaCreacionDesc(
            Long empresaId, TipoEntidad tipoEntidad, Long entidadId);

    List<Documento> findByEmpresaIdAndTipoDocumentoOrderByFechaCreacionDesc(
            Long empresaId, TipoDocumento tipoDocumento);

    @Query("SELECT d FROM Documento d WHERE d.empresaId = :empresaId " +
           "AND d.tipoEntidad = :tipoEntidad AND d.entidadId = :entidadId " +
           "AND d.tipoDocumento = :tipoDocumento")
    List<Documento> findByEntidadAndTipo(
            @Param("empresaId") Long empresaId,
            @Param("tipoEntidad") TipoEntidad tipoEntidad,
            @Param("entidadId") Long entidadId,
            @Param("tipoDocumento") TipoDocumento tipoDocumento);

    @Query("SELECT COUNT(d) FROM Documento d WHERE d.empresaId = :empresaId " +
           "AND d.tipoEntidad = :tipoEntidad AND d.entidadId = :entidadId")
    Long countByEntidad(
            @Param("empresaId") Long empresaId,
            @Param("tipoEntidad") TipoEntidad tipoEntidad,
            @Param("entidadId") Long entidadId);

    @Query("SELECT SUM(d.tamano) FROM Documento d WHERE d.empresaId = :empresaId")
    Long sumTamanoByEmpresaId(@Param("empresaId") Long empresaId);
}
