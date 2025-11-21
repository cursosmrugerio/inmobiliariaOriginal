package com.inmobiliaria.documento;

import com.inmobiliaria.documento.dto.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Value("${app.storage.path:/tmp/documentos}")
    private String storagePath;

    public List<DocumentoDTO> getAllDocumentos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndActivoTrue(empresaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentoDTO> getDocumentosByTipo(TipoDocumento tipo) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndTipoDocumentoAndActivoTrue(empresaId, tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentoDTO> getDocumentosByEntidad(String entidadTipo, Long entidadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEntidad(empresaId, entidadTipo, entidadId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DocumentoDTO getDocumentoById(Long id) {
        return toDTO(findDocumentoById(id));
    }

    public DocumentoDTO uploadDocumento(CreateDocumentoRequest request, MultipartFile file) throws IOException {
        Long empresaId = TenantContext.getCurrentTenant();

        // Generate unique filename
        String extension = getFileExtension(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Create storage directory if not exists
        Path storageDir = Paths.get(storagePath, empresaId.toString());
        Files.createDirectories(storageDir);
        
        // Save file
        Path filePath = storageDir.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());

        Documento documento = Documento.builder()
                .empresaId(empresaId)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipoDocumento(request.getTipoDocumento())
                .nombreArchivo(file.getOriginalFilename())
                .tipoContenido(file.getContentType())
                .tamanoBytes(file.getSize())
                .rutaAlmacenamiento(filePath.toString())
                .entidadTipo(request.getEntidadTipo())
                .entidadId(request.getEntidadId())
                .activo(true)
                .build();

        return toDTO(documentoRepository.save(documento));
    }

    public DocumentoDTO updateDocumento(Long id, UpdateDocumentoRequest request) {
        Documento documento = findDocumentoById(id);

        if (request.getNombre() != null) {
            documento.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            documento.setDescripcion(request.getDescripcion());
        }
        if (request.getTipoDocumento() != null) {
            documento.setTipoDocumento(request.getTipoDocumento());
        }
        if (request.getEntidadTipo() != null) {
            documento.setEntidadTipo(request.getEntidadTipo());
        }
        if (request.getEntidadId() != null) {
            documento.setEntidadId(request.getEntidadId());
        }

        return toDTO(documentoRepository.save(documento));
    }

    public void deleteDocumento(Long id) {
        Documento documento = findDocumentoById(id);
        documento.setActivo(false);
        documentoRepository.save(documento);
    }

    public byte[] downloadDocumento(Long id) throws IOException {
        Documento documento = findDocumentoById(id);
        Path filePath = Paths.get(documento.getRutaAlmacenamiento());
        return Files.readAllBytes(filePath);
    }

    private Documento findDocumentoById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findById(id)
                .filter(d -> d.getEmpresaId().equals(empresaId) && d.isActivo())
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado: " + id));
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    private DocumentoDTO toDTO(Documento documento) {
        return DocumentoDTO.builder()
                .id(documento.getId())
                .nombre(documento.getNombre())
                .descripcion(documento.getDescripcion())
                .tipoDocumento(documento.getTipoDocumento())
                .nombreArchivo(documento.getNombreArchivo())
                .tipoContenido(documento.getTipoContenido())
                .tamanoBytes(documento.getTamanoBytes())
                .entidadTipo(documento.getEntidadTipo())
                .entidadId(documento.getEntidadId())
                .createdAt(documento.getCreatedAt())
                .updatedAt(documento.getUpdatedAt())
                .build();
    }
}
