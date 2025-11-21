package com.inmobiliaria.documento.service;

import com.inmobiliaria.documento.domain.*;
import com.inmobiliaria.documento.dto.*;
import com.inmobiliaria.documento.repository.DocumentoRepository;
import com.inmobiliaria.shared.tenant.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<DocumentoDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdOrderByFechaCreacionDesc(empresaId)
                .stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoDTO findById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));
        return DocumentoDTO.fromEntity(documento);
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> findByEntidad(TipoEntidad tipoEntidad, Long entidadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndTipoEntidadAndEntidadId(empresaId, tipoEntidad, entidadId)
                .stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> findByTipo(TipoDocumento tipoDocumento) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndTipoDocumento(empresaId, tipoDocumento)
                .stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentoDTO create(CreateDocumentoRequest request, MultipartFile file) throws IOException {
        Long empresaId = TenantContext.getCurrentTenant();

        String rutaArchivo = fileStorageService.storeFile(file, empresaId);

        Documento documento = Documento.builder()
                .empresaId(empresaId)
                .nombre(request.getNombre())
                .nombreOriginal(file.getOriginalFilename())
                .tipoDocumento(request.getTipoDocumento())
                .tipoEntidad(request.getTipoEntidad())
                .entidadId(request.getEntidadId())
                .rutaArchivo(rutaArchivo)
                .tipoMime(file.getContentType())
                .tamanio(file.getSize())
                .descripcion(request.getDescripcion())
                .fechaDocumento(request.getFechaDocumento())
                .fechaVencimiento(request.getFechaVencimiento())
                .activo(true)
                .build();

        documento = documentoRepository.save(documento);
        log.info("Documento creado: {} para entidad {} {}", documento.getId(),
                request.getTipoEntidad(), request.getEntidadId());

        return DocumentoDTO.fromEntity(documento);
    }

    @Transactional
    public DocumentoDTO update(Long id, UpdateDocumentoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        if (request.getNombre() != null) {
            documento.setNombre(request.getNombre());
        }
        if (request.getTipoDocumento() != null) {
            documento.setTipoDocumento(request.getTipoDocumento());
        }
        if (request.getDescripcion() != null) {
            documento.setDescripcion(request.getDescripcion());
        }
        if (request.getFechaDocumento() != null) {
            documento.setFechaDocumento(request.getFechaDocumento());
        }
        if (request.getFechaVencimiento() != null) {
            documento.setFechaVencimiento(request.getFechaVencimiento());
        }
        if (request.getActivo() != null) {
            documento.setActivo(request.getActivo());
        }

        documento = documentoRepository.save(documento);
        log.info("Documento actualizado: {}", id);

        return DocumentoDTO.fromEntity(documento);
    }

    @Transactional
    public void delete(Long id) throws IOException {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        fileStorageService.deleteFile(documento.getRutaArchivo());
        documentoRepository.delete(documento);
        log.info("Documento eliminado: {}", id);
    }

    public byte[] downloadFile(Long id) throws IOException {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        return fileStorageService.loadFile(documento.getRutaArchivo());
    }

    @Transactional(readOnly = true)
    public Documento getDocumentoEntity(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));
    }
}
