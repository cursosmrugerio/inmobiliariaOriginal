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
    private String tipoMime;
    private Long tamanio;
    private String descripcion;
    private LocalDateTime fechaDocumento;
    private LocalDateTime fechaVencimiento;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public static DocumentoDTO fromEntity(Documento entity) {
        return DocumentoDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .nombreOriginal(entity.getNombreOriginal())
                .tipoDocumento(entity.getTipoDocumento())
                .tipoEntidad(entity.getTipoEntidad())
                .entidadId(entity.getEntidadId())
                .tipoMime(entity.getTipoMime())
                .tamanio(entity.getTamanio())
                .descripcion(entity.getDescripcion())
                .fechaDocumento(entity.getFechaDocumento())
                .fechaVencimiento(entity.getFechaVencimiento())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
