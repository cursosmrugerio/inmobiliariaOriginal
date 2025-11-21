package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.domain.TipoDocumento;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentoRequest {
    private String nombre;
    private TipoDocumento tipoDocumento;
    private String descripcion;
    private LocalDateTime fechaDocumento;
    private LocalDateTime fechaVencimiento;
    private Boolean activo;
}
