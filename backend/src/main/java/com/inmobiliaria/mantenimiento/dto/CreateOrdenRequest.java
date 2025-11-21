package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.PrioridadOrden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateOrdenRequest {
    @NotNull(message = "La propiedad es requerida")
    private Long propiedadId;
    
    private Long proveedorId;
    
    @NotBlank(message = "El título es requerido")
    private String titulo;
    
    private String descripcion;
    private PrioridadOrden prioridad;
    private LocalDate fechaProgramada;
    private BigDecimal costoEstimado;
    private String notas;
}
