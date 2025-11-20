package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.PersonaRol;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PersonaRolDTO {
    private Long id;
    private Integer rolId;
    private String rolNombre;
    private LocalDate fechaAsignacion;
    private boolean activo;

    public static PersonaRolDTO fromEntity(PersonaRol personaRol) {
        return PersonaRolDTO.builder()
                .id(personaRol.getId())
                .rolId(personaRol.getRol().getId())
                .rolNombre(personaRol.getRol().getNombre())
                .fechaAsignacion(personaRol.getFechaAsignacion())
                .activo(personaRol.isActivo())
                .build();
    }
}
