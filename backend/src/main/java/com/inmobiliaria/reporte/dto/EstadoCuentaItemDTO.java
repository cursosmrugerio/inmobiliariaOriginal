package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EstadoCuentaItemDTO {
    private LocalDate fecha;
    private String concepto;
    private String tipo; // CARGO, ABONO
    private BigDecimal cargo;
    private BigDecimal abono;
    private BigDecimal saldo;
    private String referencia;
    private Integer diasVencido;
    private String estado;
}
