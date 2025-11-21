package com.inmobiliaria.pago.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerarCargosFijosRequest {

    @NotNull(message = "El mes es requerido")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;

    @NotNull(message = "El año es requerido")
    @Min(value = 2020, message = "El año debe ser válido")
    private Integer anio;

    private Long contratoId;
}
