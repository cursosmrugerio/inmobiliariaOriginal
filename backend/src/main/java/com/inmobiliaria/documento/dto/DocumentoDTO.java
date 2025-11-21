package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.domain.Documento;
import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDTO {
    private Long id;
    private String nombre;
    private String nombreOriginal;
    private TipoDocumento tipoDocumento;
    private TipoEntidad tipoEntidad;
    private Long entidadId;
    private String contentType;
    private Long tamano;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String creadoPor;

    public static DocumentoDTO fromEntity(Documento entity) {
        return DocumentoDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .nombreOriginal(entity.getNombreOriginal())
                .tipoDocumento(entity.getTipoDocumento())
                .tipoEntidad(entity.getTipoEntidad())
                .entidadId(entity.getEntidadId())
                .contentType(entity.getContentType())
                .tamano(entity.getTamano())
                .descripcion(entity.getDescripcion())
                .fechaCreacion(entity.getFechaCreacion())
                .creadoPor(entity.getCreadoPor())
                .build();
    }
}
