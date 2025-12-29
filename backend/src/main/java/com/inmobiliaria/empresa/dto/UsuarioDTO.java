package com.inmobiliaria.empresa.dto;

import com.inmobiliaria.empresa.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private Usuario.Rol rol;
    private Boolean activo;
    private Long empresaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .empresaId(usuario.getEmpresaId())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }

    public static UsuarioDTO fromEntityBasic(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .build();
    }
}
