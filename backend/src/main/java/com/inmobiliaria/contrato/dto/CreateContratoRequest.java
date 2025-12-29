package com.inmobiliaria.contrato.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateContratoRequest {

    private String numeroContrato;

    @NotNull(message = "La propiedad es requerida")
    private Long propiedadId;

    @NotNull(message = "El arrendatario es requerido")
    private Long arrendatarioId;

    private Long avalId;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDate fechaFin;

    @NotNull(message = "El día de pago es requerido")
    @Min(value = 1, message = "El día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "El día de pago debe ser entre 1 y 31")
    private Integer diaPago;

    @NotNull(message = "El monto de renta es requerido")
    @DecimalMin(value = "0.01", message = "El monto de renta debe ser mayor a 0")
    private BigDecimal montoRenta;

    @DecimalMin(value = "0", message = "El depósito no puede ser negativo")
    private BigDecimal montoDeposito;

    @DecimalMin(value = "0", message = "La fianza no puede ser negativa")
    private BigDecimal montoFianza;

    @DecimalMin(value = "0", message = "La penalidad no puede ser negativa")
    private BigDecimal montoPenalidadDiaria;

    @Min(value = 0, message = "Los días de gracia no pueden ser negativos")
    private Integer diasGracia;

    @DecimalMin(value = "0", message = "El porcentaje de incremento no puede ser negativo")
    @DecimalMax(value = "100", message = "El porcentaje de incremento no puede ser mayor a 100")
    private BigDecimal porcentajeIncrementoAnual;

    private String condiciones;

    private String notas;
}
