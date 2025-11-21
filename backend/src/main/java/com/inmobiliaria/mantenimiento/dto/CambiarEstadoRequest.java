package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoRequest {
    @NotNull(message = "El estado es requerido")
    private EstadoOrden estado;

    private String comentario;
    private BigDecimal costoFinal;
    private String notasCierre;
}
