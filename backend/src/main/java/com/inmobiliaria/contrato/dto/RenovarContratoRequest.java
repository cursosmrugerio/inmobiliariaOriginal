package com.inmobiliaria.contrato.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RenovarContratoRequest {

    @NotNull(message = "La nueva fecha de fin es requerida")
    private LocalDate nuevaFechaFin;

    @DecimalMin(value = "0.01", message = "El nuevo monto de renta debe ser mayor a 0")
    private BigDecimal nuevoMontoRenta;

    private Long nuevoAvalId;

    private String nuevasCondiciones;

    private String notas;

    private boolean aplicarIncrementoAnual = false;
}
