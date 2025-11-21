package com.inmobiliaria.cobranza.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResumenCobranzaDTO {
    private BigDecimal totalCarteraVencida;
    private BigDecimal totalPenalidades;
    private BigDecimal totalGeneral;
    private Integer cantidadCuentasVencidas;

    // Por clasificación de antigüedad
    private BigDecimal montoVigente;
    private BigDecimal monto1a30;
    private BigDecimal monto31a60;
    private BigDecimal monto61a90;
    private BigDecimal montoMas90;

    private Integer cantidadVigente;
    private Integer cantidad1a30;
    private Integer cantidad31a60;
    private Integer cantidad61a90;
    private Integer cantidadMas90;

    // Por estado de cobranza
    private Integer pendientes;
    private Integer enGestion;
    private Integer promesasPago;
    private Integer parcialmentePagados;
}
