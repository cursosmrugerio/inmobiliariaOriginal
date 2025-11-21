package com.inmobiliaria.notificacion.dto;

import com.inmobiliaria.notificacion.domain.CategoriaNotificacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfiguracionRequest {

    @NotNull(message = "La categoría es requerida")
    private CategoriaNotificacion categoria;

    private Boolean emailHabilitado;
    private Boolean whatsappHabilitado;

    @Min(value = 0, message = "Los días de anticipación deben ser positivos")
    private Integer diasAnticipacion;

    @Min(value = 1, message = "La frecuencia debe ser al menos 1 día")
    private Integer frecuenciaRecordatorio;

    @Min(value = 1, message = "El máximo de intentos debe ser al menos 1")
    private Integer maxIntentos;

    private String plantillaEmail;
    private String plantillaWhatsapp;
    private Boolean activo;
}
