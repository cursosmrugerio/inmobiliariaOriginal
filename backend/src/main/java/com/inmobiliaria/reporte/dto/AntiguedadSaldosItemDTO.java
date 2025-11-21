package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AntiguedadSaldosItemDTO {
    private Long personaId;
    private String nombreCliente;
    private Long propiedadId;
    private String direccionPropiedad;
    private BigDecimal vigente;
    private BigDecimal vencido1a30;
    private BigDecimal vencido31a60;
    private BigDecimal vencido61a90;
    private BigDecimal vencidoMas90;
    private BigDecimal totalVencido;
    private BigDecimal saldoTotal;
    private Integer cantidadDocumentos;
}
