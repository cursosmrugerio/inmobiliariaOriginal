package com.inmobiliaria.contrato.dto;

import com.inmobiliaria.contrato.EstadoContrato;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateContratoRequest {

    private String numeroContrato;

    private Long propiedadId;

    private Long arrendatarioId;

    private Long avalId;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Min(value = 1, message = "El día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "El día de pago debe ser entre 1 y 31")
    private Integer diaPago;

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

    private EstadoContrato estado;

    private String condiciones;

    private String notas;

    private Boolean activo;
}
