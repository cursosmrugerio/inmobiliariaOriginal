package com.inmobiliaria.notificacion.dto;

import com.inmobiliaria.notificacion.domain.CategoriaNotificacion;
import com.inmobiliaria.notificacion.domain.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificacionRequest {

    private Long personaId;

    @NotNull(message = "El tipo de notificación es requerido")
    private TipoNotificacion tipo;

    @NotNull(message = "La categoría es requerida")
    private CategoriaNotificacion categoria;

    @NotBlank(message = "El destinatario es requerido")
    private String destinatario;

    @NotBlank(message = "El asunto es requerido")
    private String asunto;

    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;

    private Long referenciaId;
    private String referenciaTipo;
    private LocalDateTime fechaProgramada;
}
