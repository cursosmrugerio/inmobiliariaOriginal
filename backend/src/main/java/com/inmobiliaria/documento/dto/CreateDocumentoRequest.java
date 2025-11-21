package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.domain.TipoDocumento;
import com.inmobiliaria.documento.domain.TipoEntidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentoRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotNull(message = "El tipo de documento es requerido")
    private TipoDocumento tipoDocumento;

    @NotNull(message = "El tipo de entidad es requerido")
    private TipoEntidad tipoEntidad;

    @NotNull(message = "El ID de la entidad es requerido")
    private Long entidadId;

    private String descripcion;
}
