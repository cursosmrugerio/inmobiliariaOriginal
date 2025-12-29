package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CreateProveedorRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private String razonSocial;
    private String rfc;
    private String telefonoPrincipal;
    private String telefonoSecundario;

    @Email(message = "Email inválido")
    private String email;

    private String direccion;
    private String codigoPostal;
    private String ciudad;
    private String estado;
    private String nombreContacto;
    private Set<CategoriaMantenimiento> categorias;
    private String notas;
}
