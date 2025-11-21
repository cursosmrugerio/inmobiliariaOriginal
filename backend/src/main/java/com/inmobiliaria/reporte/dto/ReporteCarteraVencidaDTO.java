package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReporteCarteraVencidaDTO {
    // Información general
    private Long empresaId;
    private String nombreEmpresa;
    private LocalDate fechaCorte;
    private LocalDate fechaGeneracion;

    // Resumen
    private BigDecimal totalCartera;
    private BigDecimal totalPenalidades;
    private BigDecimal totalGeneral;
    private Integer cantidadCuentas;

    // Por estado de cobranza
    private Integer cuentasPendientes;
    private Integer cuentasEnGestion;
    private Integer cuentasConPromesa;
    private Integer cuentasParcialmentePagadas;

    private BigDecimal montoPendiente;
    private BigDecimal montoEnGestion;
    private BigDecimal montoConPromesa;
    private BigDecimal montoParcialmentePagado;

    // Por clasificación de antigüedad
    private BigDecimal montoVigente;
    private BigDecimal monto1a30;
    private BigDecimal monto31a60;
    private BigDecimal monto61a90;
    private BigDecimal montoMas90;

    // Detalle
    private List<CarteraVencidaItemDTO> detalle;

    @Data
    @Builder
    public static class CarteraVencidaItemDTO {
        private Long id;
        private Long personaId;
        private String nombreCliente;
        private Long propiedadId;
        private String direccionPropiedad;
        private String concepto;
        private LocalDate fechaVencimiento;
        private Integer diasVencido;
        private String clasificacion;
        private BigDecimal montoOriginal;
        private BigDecimal montoPendiente;
        private BigDecimal montoPenalidad;
        private BigDecimal montoTotal;
        private String estadoCobranza;
        private String ultimaGestion;
        private LocalDate fechaUltimaGestion;
    }
}
