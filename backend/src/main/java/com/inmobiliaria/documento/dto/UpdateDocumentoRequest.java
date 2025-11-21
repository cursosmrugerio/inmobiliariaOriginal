package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.domain.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentoRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private TipoDocumento tipoDocumento;

    private String descripcion;
}
