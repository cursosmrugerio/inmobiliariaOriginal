package com.inmobiliaria.notificacion.dto;

import com.inmobiliaria.notificacion.domain.CategoriaNotificacion;
import com.inmobiliaria.notificacion.domain.ConfiguracionNotificacion;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNotificacionDTO {
    private Long id;
    private CategoriaNotificacion categoria;
    private Boolean emailHabilitado;
    private Boolean whatsappHabilitado;
    private Integer diasAnticipacion;
    private Integer frecuenciaRecordatorio;
    private Integer maxIntentos;
    private String plantillaEmail;
    private String plantillaWhatsapp;
    private Boolean activo;

    public static ConfiguracionNotificacionDTO fromEntity(ConfiguracionNotificacion entity) {
        return ConfiguracionNotificacionDTO.builder()
                .id(entity.getId())
                .categoria(entity.getCategoria())
                .emailHabilitado(entity.getEmailHabilitado())
                .whatsappHabilitado(entity.getWhatsappHabilitado())
                .diasAnticipacion(entity.getDiasAnticipacion())
                .frecuenciaRecordatorio(entity.getFrecuenciaRecordatorio())
                .maxIntentos(entity.getMaxIntentos())
                .plantillaEmail(entity.getPlantillaEmail())
                .plantillaWhatsapp(entity.getPlantillaWhatsapp())
                .activo(entity.getActivo())
                .build();
    }
}
