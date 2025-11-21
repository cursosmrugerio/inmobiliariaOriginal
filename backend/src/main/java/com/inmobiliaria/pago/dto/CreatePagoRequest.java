package com.inmobiliaria.pago.dto;

import com.inmobiliaria.pago.TipoPago;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePagoRequest {

    @NotNull(message = "El contrato es requerido")
    private Long contratoId;

    @NotNull(message = "La persona es requerida")
    private Long personaId;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "El tipo de pago es requerido")
    private TipoPago tipoPago;

    @NotNull(message = "La fecha de pago es requerida")
    private LocalDate fechaPago;

    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String referencia;

    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    private String banco;

    @Size(max = 50, message = "El número de cheque no puede exceder 50 caracteres")
    private String numeroCheque;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;

    private String comprobanteUrl;

    private List<Long> cargoIds;

    private Boolean aplicarAutomaticamente = true;
}
