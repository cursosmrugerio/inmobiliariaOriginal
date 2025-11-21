package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AntiguedadSaldosDTO {
    // Información general
    private Long empresaId;
    private String nombreEmpresa;
    private LocalDate fechaCorte;
    private LocalDate fechaGeneracion;

    // Totales por antigüedad
    private BigDecimal totalVigente;
    private BigDecimal totalVencido1a30;
    private BigDecimal totalVencido31a60;
    private BigDecimal totalVencido61a90;
    private BigDecimal totalVencidoMas90;
    private BigDecimal totalVencido;
    private BigDecimal totalGeneral;

    // Contadores
    private Integer cantidadClientes;
    private Integer cantidadDocumentos;

    // Porcentajes
    private BigDecimal porcentajeVigente;
    private BigDecimal porcentajeVencido;

    // Detalle por cliente
    private List<AntiguedadSaldosItemDTO> detalle;
}
