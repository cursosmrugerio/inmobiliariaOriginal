package com.inmobiliaria.pago.dto;

import com.inmobiliaria.pago.TipoCargo;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCargoRequest {

    @NotNull(message = "El contrato es requerido")
    private Long contratoId;

    @NotNull(message = "El tipo de cargo es requerido")
    private TipoCargo tipoCargo;

    @NotBlank(message = "El concepto es requerido")
    @Size(max = 200, message = "El concepto no puede exceder 200 caracteres")
    private String concepto;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoOriginal;

    @NotNull(message = "La fecha de cargo es requerida")
    private LocalDate fechaCargo;

    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate fechaVencimiento;

    private Boolean esCargoFijo = false;

    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer periodoMes;

    private Integer periodoAnio;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
