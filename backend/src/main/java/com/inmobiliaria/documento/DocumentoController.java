package com.inmobiliaria.documento;

import com.inmobiliaria.documento.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping
    public ResponseEntity<List<DocumentoDTO>> getAllDocumentos(
            @RequestParam(required = false) TipoDocumento tipo) {
        List<DocumentoDTO> documentos = tipo != null
                ? documentoService.getDocumentosByTipo(tipo)
                : documentoService.getAllDocumentos();
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> getDocumentoById(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.getDocumentoById(id));
    }

    @GetMapping("/entidad/{entidadTipo}/{entidadId}")
    public ResponseEntity<List<DocumentoDTO>> getDocumentosByEntidad(
            @PathVariable String entidadTipo,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(documentoService.getDocumentosByEntidad(entidadTipo, entidadId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> uploadDocumento(
            @RequestPart("metadata") @Valid CreateDocumentoRequest request,
            @RequestPart("file") MultipartFile file) throws IOException {
        DocumentoDTO documento = documentoService.uploadDocumento(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDTO> updateDocumento(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentoRequest request) {
        return ResponseEntity.ok(documentoService.updateDocumento(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id) throws IOException {
        DocumentoDTO documento = documentoService.getDocumentoById(id);
        byte[] content = documentoService.downloadDocumento(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                .contentType(MediaType.parseMediaType(documento.getTipoContenido() != null ? documento.getTipoContenido() : "application/octet-stream"))
                .body(content);
    }
}
