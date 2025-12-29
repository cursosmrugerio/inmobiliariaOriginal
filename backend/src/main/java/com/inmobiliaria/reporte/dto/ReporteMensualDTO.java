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
public class ReporteMensualDTO {

    private Long empresaId;
    private String nombreEmpresa;
    private Integer mes;
    private Integer anio;
    private String periodoDescripcion;
    private LocalDate fechaGeneracion;

    // Resumen de propiedades
    private Integer totalPropiedades;
    private Integer propiedadesOcupadas;
    private Integer propiedadesDisponibles;
    private BigDecimal porcentajeOcupacion;

    // Resumen de contratos
    private Integer contratosActivos;
    private Integer contratosPorVencer;
    private Integer contratosVencidos;
    private Integer contratosNuevos;
    private Integer contratosTerminados;
    private Integer contratosRenovados;

    // Resumen financiero
    private BigDecimal ingresosPorRenta;
    private BigDecimal ingresosPorOtrosConceptos;
    private BigDecimal totalIngresos;
    private BigDecimal rentaEsperada;
    private BigDecimal rentaCobrada;
    private BigDecimal porcentajeCobranza;

    // Cartera
    private BigDecimal carteraVigente;
    private BigDecimal carteraVencida;
    private BigDecimal carteraTotal;
    private Integer clientesConAdeudo;
    private Integer clientesAlCorriente;

    // Detalle por propiedad
    private List<PropiedadMensualDTO> detallePropiedades;

    // Detalle de ingresos
    private List<IngresoMensualDTO> detalleIngresos;

    // Top morosos
    private List<MorosoDTO> topMorosos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropiedadMensualDTO {
        private Long propiedadId;
        private String direccion;
        private String tipoPropiedad;
        private String estadoOcupacion;
        private String arrendatario;
        private BigDecimal rentaMensual;
        private BigDecimal rentaCobrada;
        private BigDecimal saldoPendiente;
        private String estadoPago;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngresoMensualDTO {
        private LocalDate fecha;
        private String concepto;
        private String propiedad;
        private String cliente;
        private BigDecimal monto;
        private String tipoPago;
        private String referencia;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MorosoDTO {
        private Long personaId;
        private String nombre;
        private String propiedad;
        private BigDecimal montoAdeudado;
        private Integer diasVencido;
        private String estadoCobranza;
    }
}
