package com.inmobiliaria.documento.service;

import com.inmobiliaria.documento.config.FileStorageConfig;
import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import com.inmobiliaria.documento.dto.CreateDocumentoRequest;
import com.inmobiliaria.documento.dto.DocumentoDTO;
import com.inmobiliaria.documento.dto.UpdateDocumentoRequest;
import com.inmobiliaria.documento.repository.DocumentoRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final FileStorageConfig fileStorageConfig;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Directorio de almacenamiento creado: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento", ex);
        }
    }

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
        Documento documento = documentoRepository.findById(id)
                .filter(d -> d.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));
        return DocumentoDTO.fromEntity(documento);
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> findByEntidad(TipoEntidad tipoEntidad, Long entidadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndTipoEntidadAndEntidadIdOrderByFechaCreacionDesc(
                empresaId, tipoEntidad, entidadId)
                .stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> findByTipoDocumento(TipoDocumento tipoDocumento) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.findByEmpresaIdAndTipoDocumentoOrderByFechaCreacionDesc(
                empresaId, tipoDocumento)
                .stream()
                .map(DocumentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentoDTO upload(MultipartFile file, CreateDocumentoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = String.format("%s_%s_%s%s",
                empresaId, timestamp, UUID.randomUUID().toString().substring(0, 8), extension);

        // Create directory structure: uploads/{empresaId}/{tipoEntidad}/
        Path targetLocation = this.fileStorageLocation
                .resolve(empresaId.toString())
                .resolve(request.getTipoEntidad().name().toLowerCase());

        try {
            Files.createDirectories(targetLocation);
            Path filePath = targetLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create documento entity
            Documento documento = Documento.builder()
                    .empresaId(empresaId)
                    .nombre(request.getNombre())
                    .nombreOriginal(originalFilename)
                    .tipoDocumento(request.getTipoDocumento())
                    .tipoEntidad(request.getTipoEntidad())
                    .entidadId(request.getEntidadId())
                    .contentType(file.getContentType())
                    .tamano(file.getSize())
                    .rutaArchivo(filePath.toString())
                    .descripcion(request.getDescripcion())
                    .build();

            documento = documentoRepository.save(documento);
            log.info("Documento subido: {} para entidad {} {}",
                    documento.getId(), request.getTipoEntidad(), request.getEntidadId());

            return DocumentoDTO.fromEntity(documento);
        } catch (IOException ex) {
            throw new RuntimeException("Error al almacenar el archivo", ex);
        }
    }

    @Transactional
    public DocumentoDTO update(Long id, UpdateDocumentoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findById(id)
                .filter(d -> d.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        documento.setNombre(request.getNombre());
        if (request.getTipoDocumento() != null) {
            documento.setTipoDocumento(request.getTipoDocumento());
        }
        documento.setDescripcion(request.getDescripcion());

        documento = documentoRepository.save(documento);
        log.info("Documento actualizado: {}", documento.getId());

        return DocumentoDTO.fromEntity(documento);
    }

    @Transactional
    public void delete(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findById(id)
                .filter(d -> d.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        // Delete physical file
        try {
            Path filePath = Paths.get(documento.getRutaArchivo());
            Files.deleteIfExists(filePath);
            log.info("Archivo físico eliminado: {}", filePath);
        } catch (IOException ex) {
            log.warn("No se pudo eliminar el archivo físico: {}", documento.getRutaArchivo(), ex);
        }

        documentoRepository.delete(documento);
        log.info("Documento eliminado: {}", id);
    }

    public Resource loadFileAsResource(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Documento documento = documentoRepository.findById(id)
                .filter(d -> d.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        try {
            Path filePath = Paths.get(documento.getRutaArchivo()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error al cargar el archivo", ex);
        }
    }

    @Transactional(readOnly = true)
    public Long countByEntidad(TipoEntidad tipoEntidad, Long entidadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return documentoRepository.countByEntidad(empresaId, tipoEntidad, entidadId);
    }

    @Transactional(readOnly = true)
    public Long getTotalStorageUsed() {
        Long empresaId = TenantContext.getCurrentTenant();
        Long total = documentoRepository.sumTamanoByEmpresaId(empresaId);
        return total != null ? total : 0L;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
