package com.inmobiliaria.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaMensualDTO {

    // Información del cliente
    private Long personaId;
    private String nombreCliente;
    private String tipoPersona;
    private String rfc;
    private String email;
    private String telefono;
    private String direccion;

    // Información de la empresa
    private Long empresaId;
    private String nombreEmpresa;

    // Período
    private Integer mes;
    private Integer anio;
    private String periodoDescripcion;
    private LocalDate fechaGeneracion;

    // Saldos
    private BigDecimal saldoInicial;
    private BigDecimal totalCargos;
    private BigDecimal totalAbonos;
    private BigDecimal saldoFinal;

    // Resumen por estado
    private BigDecimal saldoVencido;
    private BigDecimal saldoPorVencer;
    private int diasPromedioVencido;

    // Movimientos del mes
    private List<MovimientoMensualDTO> movimientos;

    // Propiedades relacionadas
    private List<PropiedadResumenDTO> propiedades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoMensualDTO {
        private LocalDate fecha;
        private String concepto;
        private String tipo; // CARGO, ABONO
        private BigDecimal cargo;
        private BigDecimal abono;
        private BigDecimal saldoAcumulado;
        private String referencia;
        private String propiedad;
        private String estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropiedadResumenDTO {
        private Long propiedadId;
        private String direccion;
        private BigDecimal rentaMensual;
        private BigDecimal cargosDelMes;
        private BigDecimal pagosDelMes;
        private BigDecimal saldoPendiente;
        private String estadoPago;
    }
}
