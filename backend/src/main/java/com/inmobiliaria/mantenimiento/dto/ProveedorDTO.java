package com.inmobiliaria.mantenimiento.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String razonSocial;
    private String rfc;
    private String especialidad;
    private String telefono;
    private String email;
    private String direccion;
    private String notas;
    private LocalDateTime createdAt;
}
