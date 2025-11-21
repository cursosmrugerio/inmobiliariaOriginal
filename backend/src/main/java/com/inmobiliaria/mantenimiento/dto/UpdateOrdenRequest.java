package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import com.inmobiliaria.mantenimiento.domain.PrioridadOrden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrdenRequest {
    private Long proveedorId;
    private Long solicitanteId;

    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    @NotNull(message = "La categoría es requerida")
    private CategoriaMantenimiento categoria;

    @NotNull(message = "La prioridad es requerida")
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
