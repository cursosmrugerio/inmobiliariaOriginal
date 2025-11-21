package com.inmobiliaria.pago.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PagoAplicacionDTO {
    private Long id;
    private Long pagoId;
    private Long cargoId;
    private String cargoConcepto;
    private BigDecimal montoAplicado;
    private LocalDateTime createdAt;
}
