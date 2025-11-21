package com.inmobiliaria.documento.repository;

import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByEmpresaIdOrderByFechaCreacionDesc(Long empresaId);

    List<Documento> findByEmpresaIdAndTipoEntidadAndEntidadId(
            Long empresaId, TipoEntidad tipoEntidad, Long entidadId);

    List<Documento> findByEmpresaIdAndTipoDocumento(Long empresaId, TipoDocumento tipoDocumento);

    Optional<Documento> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Documento> findByEmpresaIdAndActivo(Long empresaId, Boolean activo);
}
