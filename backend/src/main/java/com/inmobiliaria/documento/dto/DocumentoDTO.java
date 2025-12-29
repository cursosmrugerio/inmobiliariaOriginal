package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentoDTO {
    private Long id;
    private String nombre;
    private String nombreOriginal;
    private String descripcion;
    private TipoDocumento tipoDocumento;
    private TipoEntidad tipoEntidad;
    private Long entidadId;
    private String contentType;
    private Long tamano;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static DocumentoDTO fromEntity(Documento documento) {
        if (documento == null) {
            return null;
        }
        return DocumentoDTO.builder()
                .id(documento.getId())
                .nombre(documento.getNombre())
                .nombreOriginal(documento.getNombreOriginal())
                .descripcion(documento.getDescripcion())
                .tipoDocumento(documento.getTipoDocumento())
                .tipoEntidad(documento.getTipoEntidad())
                .entidadId(documento.getEntidadId())
                .contentType(documento.getContentType())
                .tamano(documento.getTamano())
                .fechaCreacion(documento.getFechaCreacion())
                .fechaActualizacion(documento.getFechaActualizacion())
                .build();
    }
}
