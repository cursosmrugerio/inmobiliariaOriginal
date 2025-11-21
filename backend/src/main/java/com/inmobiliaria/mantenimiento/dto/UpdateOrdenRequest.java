package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.EstadoOrden;
import com.inmobiliaria.mantenimiento.PrioridadOrden;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateOrdenRequest {
    private Long proveedorId;
    private String titulo;
    private String descripcion;
    private PrioridadOrden prioridad;
    private EstadoOrden estado;
    private LocalDate fechaProgramada;
    private LocalDate fechaCompletada;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    private String notas;
}
