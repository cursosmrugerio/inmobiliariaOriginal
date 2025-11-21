package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.TipoDocumento;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private TipoDocumento tipoDocumento;
    private String nombreArchivo;
    private String tipoContenido;
    private Long tamanoBytes;
    private String entidadTipo;
    private Long entidadId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
