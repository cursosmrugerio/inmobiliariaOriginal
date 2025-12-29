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
public class FiniquitoDTO {

    private Long contratoId;
    private String numeroContrato;
    private Long empresaId;
    private String nombreEmpresa;

    // Datos del arrendatario
    private Long arrendatarioId;
    private String nombreArrendatario;
    private String rfcArrendatario;
    private String emailArrendatario;
    private String telefonoArrendatario;

    // Datos de la propiedad
    private Long propiedadId;
    private String direccionPropiedad;
    private String tipoPropiedad;

    // Datos del contrato
    private LocalDate fechaInicioContrato;
    private LocalDate fechaFinContrato;
    private LocalDate fechaTerminacion;
    private String motivoTerminacion;
    private BigDecimal montoRentaMensual;
    private BigDecimal montoDeposito;

    // Resumen financiero
    private BigDecimal totalRentasPagadas;
    private BigDecimal totalRentasPendientes;
    private BigDecimal totalCargosAdicionales;
    private BigDecimal totalPagosRealizados;
    private BigDecimal saldoPendiente;
    private BigDecimal depositoADevolver;
    private BigDecimal deduccionesDeposito;
    private BigDecimal montoLiquidacion;

    // Detalle de conceptos
    private List<ConceptoFiniquitoDTO> conceptos;

    // Metadatos
    private LocalDate fechaGeneracion;
    private String generadoPor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConceptoFiniquitoDTO {
        private String concepto;
        private String tipo; // CARGO, ABONO, DEPOSITO, DEDUCCION
        private LocalDate fecha;
        private BigDecimal monto;
        private String estado;
        private String notas;
    }
}
