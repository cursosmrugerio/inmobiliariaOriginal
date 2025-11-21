package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProyeccionCobranzaReporteDTO {
    // Información general
    private Long empresaId;
    private String nombreEmpresa;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private LocalDate fechaGeneracion;

    // Totales del periodo
    private BigDecimal totalProyectado;
    private BigDecimal totalCobrado;
    private BigDecimal totalPendiente;
    private BigDecimal porcentajeCumplimiento;

    // Estadísticas
    private Integer totalContratosActivos;
    private Integer totalPagosEsperados;
    private Integer totalPagosRecibidos;

    // Detalle por mes
    private List<ProyeccionMesDTO> detalleMensual;

    @Data
    @Builder
    public static class ProyeccionMesDTO {
        private LocalDate periodo;
        private String mesAnio;
        private BigDecimal montoProyectado;
        private BigDecimal montoCobrado;
        private BigDecimal diferencia;
        private BigDecimal porcentajeCumplimiento;
        private Integer cantidadContratos;
        private Integer pagosEsperados;
        private Integer pagosRecibidos;
    }
}
