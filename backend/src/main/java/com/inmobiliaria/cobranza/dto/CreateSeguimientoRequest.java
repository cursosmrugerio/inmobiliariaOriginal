package com.inmobiliaria.cobranza.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateSeguimientoRequest {
    @NotNull(message = "El ID de cartera vencida es requerido")
    private Long carteraVencidaId;

    @NotNull(message = "El tipo de contacto es requerido")
    private String tipoContacto;

    private LocalDateTime fechaContacto;

    private String descripcion;

    private String resultado;

    private LocalDate fechaPromesaPago;

    private BigDecimal montoPromesa;

    private String proximaAccion;

    private LocalDate fechaProximaAccion;
}
