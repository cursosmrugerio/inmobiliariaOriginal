package com.inmobiliaria.empresa.dto;

import com.inmobiliaria.empresa.Empresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

    private Long id;
    private String nombre;
    private String rfc;
    private String direccion;
    private String telefono;
    private String email;
    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmpresaDTO fromEntity(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .rfc(empresa.getRfc())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                .activo(empresa.isActivo())
                .createdAt(empresa.getCreatedAt())
                .updatedAt(empresa.getUpdatedAt())
                .build();
    }
}
