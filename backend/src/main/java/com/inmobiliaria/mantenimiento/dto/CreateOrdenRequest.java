package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.PrioridadOrden;
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
    private Long solicitanteId;

    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    private CategoriaMantenimiento categoria;
    private PrioridadOrden prioridad;
    private LocalDate fechaProgramada;
    private BigDecimal costoEstimado;
    private String notasTecnicas;
}
