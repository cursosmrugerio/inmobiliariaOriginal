package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.EstadoOrden;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSeguimientoRequest {
    @NotBlank(message = "El comentario es requerido")
    private String comentario;
    private EstadoOrden nuevoEstado;
}
