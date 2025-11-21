package com.inmobiliaria.documento.controller;

import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import com.inmobiliaria.documento.dto.*;
import com.inmobiliaria.documento.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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
    public ResponseEntity<List<DocumentoDTO>> findByTipo(@PathVariable TipoDocumento tipoDocumento) {
        return ResponseEntity.ok(documentoService.findByTipo(tipoDocumento));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoDTO> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipoDocumento") TipoDocumento tipoDocumento,
            @RequestParam("tipoEntidad") TipoEntidad tipoEntidad,
            @RequestParam("entidadId") Long entidadId,
            @RequestParam(value = "descripcion", required = false) String descripcion) throws IOException {

        CreateDocumentoRequest request = CreateDocumentoRequest.builder()
                .nombre(nombre)
                .tipoDocumento(tipoDocumento)
                .tipoEntidad(tipoEntidad)
                .entidadId(entidadId)
                .descripcion(descripcion)
                .build();

        return ResponseEntity.ok(documentoService.create(request, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentoRequest request) {
        return ResponseEntity.ok(documentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        documentoService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws IOException {
        Documento documento = documentoService.getDocumentoEntity(id);
        byte[] fileContent = documentoService.downloadFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                documento.getTipoMime() != null ? documento.getTipoMime() : "application/octet-stream"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(documento.getNombreOriginal() != null ? documento.getNombreOriginal() : documento.getNombre())
                .build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
