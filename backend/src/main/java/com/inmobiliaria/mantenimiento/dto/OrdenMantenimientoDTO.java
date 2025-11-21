package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenMantenimientoDTO {
    private Long id;
    private Long empresaId;
    private String numeroOrden;
    private Long propiedadId;
    private String propiedadNombre;
    private Long proveedorId;
    private String proveedorNombre;
    private Long solicitanteId;
    private String solicitanteNombre;
    private String titulo;
    private String descripcion;
    private CategoriaMantenimiento categoria;
    private PrioridadOrden prioridad;
    private EstadoOrden estado;
    private LocalDate fechaSolicitud;
    private LocalDate fechaProgramada;
    private LocalDate fechaInicio;
    private LocalDate fechaCompletada;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    private String notasTecnicas;
    private String notasCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;

    public static OrdenMantenimientoDTO fromEntity(OrdenMantenimiento orden) {
        return OrdenMantenimientoDTO.builder()
                .id(orden.getId())
                .empresaId(orden.getEmpresaId())
                .numeroOrden(orden.getNumeroOrden())
                .propiedadId(orden.getPropiedadId())
                .proveedorId(orden.getProveedorId())
                .solicitanteId(orden.getSolicitanteId())
                .titulo(orden.getTitulo())
                .descripcion(orden.getDescripcion())
                .categoria(orden.getCategoria())
                .prioridad(orden.getPrioridad())
                .estado(orden.getEstado())
                .fechaSolicitud(orden.getFechaSolicitud())
                .fechaProgramada(orden.getFechaProgramada())
                .fechaInicio(orden.getFechaInicio())
                .fechaCompletada(orden.getFechaCompletada())
                .costoEstimado(orden.getCostoEstimado())
                .costoFinal(orden.getCostoFinal())
                .notasTecnicas(orden.getNotasTecnicas())
                .notasCierre(orden.getNotasCierre())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaActualizacion(orden.getFechaActualizacion())
                .creadoPor(orden.getCreadoPor())
                .build();
    }
}
