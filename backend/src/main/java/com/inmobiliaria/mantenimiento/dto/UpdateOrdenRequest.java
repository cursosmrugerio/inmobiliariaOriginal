package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import com.inmobiliaria.mantenimiento.domain.PrioridadOrden;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateOrdenRequest {
    private Long proveedorId;
    private Long solicitanteId;
    private String titulo;
    private String descripcion;
    private CategoriaMantenimiento categoria;
    private PrioridadOrden prioridad;
    private EstadoOrden estado;
    private LocalDate fechaProgramada;
    private LocalDate fechaInicio;
    private LocalDate fechaCompletada;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    private String notasTecnicas;
    private String notasCierre;
}
