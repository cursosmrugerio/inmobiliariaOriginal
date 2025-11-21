package com.inmobiliaria.mantenimiento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProveedorRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    private String razonSocial;
    private String rfc;
    private String especialidad;
    private String telefono;
    private String email;
    private String direccion;
    private String notas;
}
