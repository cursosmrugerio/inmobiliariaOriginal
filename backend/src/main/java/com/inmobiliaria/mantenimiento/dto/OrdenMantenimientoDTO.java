package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.EstadoOrden;
import com.inmobiliaria.mantenimiento.PrioridadOrden;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OrdenMantenimientoDTO {
    private Long id;
    private String numeroOrden;
    private Long propiedadId;
    private String propiedadNombre;
    private Long proveedorId;
    private String proveedorNombre;
    private String titulo;
    private String descripcion;
    private PrioridadOrden prioridad;
    private EstadoOrden estado;
    private LocalDate fechaSolicitud;
    private LocalDate fechaProgramada;
    private LocalDate fechaCompletada;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    private String notas;
    private LocalDateTime createdAt;
}
