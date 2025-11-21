package com.inmobiliaria.cobranza.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCarteraVencidaRequest {
    @NotNull(message = "El ID del contrato es requerido")
    private Long contratoId;

    @NotNull(message = "El ID de la persona es requerido")
    private Long personaId;

    @NotNull(message = "El ID de la propiedad es requerido")
    private Long propiedadId;

    @NotNull(message = "El monto original es requerido")
    @Positive(message = "El monto original debe ser positivo")
    private BigDecimal montoOriginal;

    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate fechaVencimiento;

    private String concepto;

    private BigDecimal porcentajePenalidad;
}
