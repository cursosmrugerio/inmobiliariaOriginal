package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import com.inmobiliaria.mantenimiento.domain.SeguimientoOrden;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeguimientoOrdenDTO {
    private Long id;
    private Long ordenId;
    private EstadoOrden estadoAnterior;
    private EstadoOrden estadoNuevo;
    private String comentario;
    private String usuario;
    private LocalDateTime fechaRegistro;

    public static SeguimientoOrdenDTO fromEntity(SeguimientoOrden seguimiento) {
        if (seguimiento == null) {
            return null;
        }
        return SeguimientoOrdenDTO.builder()
                .id(seguimiento.getId())
                .ordenId(seguimiento.getOrdenId())
                .estadoAnterior(seguimiento.getEstadoAnterior())
                .estadoNuevo(seguimiento.getEstadoNuevo())
                .comentario(seguimiento.getComentario())
                .usuario(seguimiento.getUsuario())
                .fechaRegistro(seguimiento.getFechaRegistro())
                .build();
    }
}
