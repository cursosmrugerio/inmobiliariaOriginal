package com.inmobiliaria.pago.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AplicarPagoRequest {

    @NotNull(message = "El cargo es requerido")
    private Long cargoId;

    @NotNull(message = "El monto a aplicar es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoAplicar;
}
