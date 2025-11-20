package com.inmobiliaria.propiedad.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPropietarioRequest {

    @NotNull(message = "El propietario es requerido")
    private Long propietarioId;

    private BigDecimal porcentajePropiedad;
    private LocalDate fechaAdquisicion;
    private boolean esPrincipal;
}
