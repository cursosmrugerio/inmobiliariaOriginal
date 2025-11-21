package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDocumentoRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de documento es requerido")
    private TipoDocumento tipoDocumento;

    private String entidadTipo;
    private Long entidadId;
}
