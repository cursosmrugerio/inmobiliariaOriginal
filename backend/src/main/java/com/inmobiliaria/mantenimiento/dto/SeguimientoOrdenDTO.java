package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.EstadoOrden;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeguimientoOrdenDTO {
    private Long id;
    private Long ordenId;
    private String comentario;
    private EstadoOrden estadoAnterior;
    private EstadoOrden estadoNuevo;
    private LocalDateTime createdAt;
}
