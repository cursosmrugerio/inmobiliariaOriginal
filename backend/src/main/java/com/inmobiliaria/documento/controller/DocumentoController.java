package com.inmobiliaria.documento.controller;

import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import com.inmobiliaria.documento.dto.CreateDocumentoRequest;
import com.inmobiliaria.documento.dto.DocumentoDTO;
import com.inmobiliaria.documento.dto.UpdateDocumentoRequest;
import com.inmobiliaria.documento.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping
    public ResponseEntity<List<DocumentoDTO>> findAll() {
        return ResponseEntity.ok(documentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @GetMapping("/entidad/{tipoEntidad}/{entidadId}")
    public ResponseEntity<List<DocumentoDTO>> findByEntidad(
            @PathVariable TipoEntidad tipoEntidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(documentoService.findByEntidad(tipoEntidad, entidadId));
    }

    @GetMapping("/tipo/{tipoDocumento}")
    public ResponseEntity<List<DocumentoDTO>> findByTipoDocumento(
            @PathVariable TipoDocumento tipoDocumento) {
        return ResponseEntity.ok(documentoService.findByTipoDocumento(tipoDocumento));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipoDocumento") TipoDocumento tipoDocumento,
            @RequestParam("tipoEntidad") TipoEntidad tipoEntidad,
            @RequestParam("entidadId") Long entidadId,
            @RequestParam(value = "descripcion", required = false) String descripcion) {

        CreateDocumentoRequest request = CreateDocumentoRequest.builder()
                .nombre(nombre)
                .tipoDocumento(tipoDocumento)
                .tipoEntidad(tipoEntidad)
                .entidadId(entidadId)
                .descripcion(descripcion)
                .build();

        return ResponseEntity.ok(documentoService.upload(file, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentoRequest request) {
        return ResponseEntity.ok(documentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        DocumentoDTO documento = documentoService.findById(id);
        Resource resource = documentoService.loadFileAsResource(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + documento.getNombreOriginal() + "\"")
                .body(resource);
    }

    @GetMapping("/entidad/{tipoEntidad}/{entidadId}/count")
    public ResponseEntity<Long> countByEntidad(
            @PathVariable TipoEntidad tipoEntidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(documentoService.countByEntidad(tipoEntidad, entidadId));
    }

    @GetMapping("/storage/used")
    public ResponseEntity<Long> getStorageUsed() {
        return ResponseEntity.ok(documentoService.getTotalStorageUsed());
    }
}
