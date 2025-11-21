package com.inmobiliaria.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EstadoCuentaDTO {
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

    // Periodo del estado de cuenta
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaGeneracion;

    // Resumen de saldos
    private BigDecimal saldoAnterior;
    private BigDecimal totalCargos;
    private BigDecimal totalAbonos;
    private BigDecimal saldoActual;
    private BigDecimal saldoVencido;
    private BigDecimal saldoPorVencer;

    // Detalle de movimientos
    private List<EstadoCuentaItemDTO> movimientos;

    // Propiedades relacionadas
    private List<String> propiedades;
}
