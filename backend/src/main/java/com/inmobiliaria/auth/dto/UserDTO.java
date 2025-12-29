package com.inmobiliaria.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
    private Long empresaId;
    private String empresaNombre;
    private boolean activo;
}
