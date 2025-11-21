package com.inmobiliaria.notificacion.dto;

import com.inmobiliaria.notificacion.domain.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long id;
    private Long personaId;
    private String personaNombre;
    private TipoNotificacion tipo;
    private CategoriaNotificacion categoria;
    private EstadoNotificacion estado;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private Long referenciaId;
    private String referenciaTipo;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnvio;
    private Integer intentos;
    private String errorMensaje;
    private LocalDateTime fechaCreacion;

    public static NotificacionDTO fromEntity(Notificacion entity) {
        return NotificacionDTO.builder()
                .id(entity.getId())
                .personaId(entity.getPersonaId())
                .tipo(entity.getTipo())
                .categoria(entity.getCategoria())
                .estado(entity.getEstado())
                .destinatario(entity.getDestinatario())
                .asunto(entity.getAsunto())
                .mensaje(entity.getMensaje())
                .referenciaId(entity.getReferenciaId())
                .referenciaTipo(entity.getReferenciaTipo())
                .fechaProgramada(entity.getFechaProgramada())
                .fechaEnvio(entity.getFechaEnvio())
                .intentos(entity.getIntentos())
                .errorMensaje(entity.getErrorMensaje())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
