package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.TipoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePersonaRequest {

    @NotNull(message = "El tipo de persona es requerido")
    private TipoPersona tipoPersona;

    // Persona Física
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres")
    private String apellidoPaterno;

    @Size(max = 100, message = "El apellido materno no puede exceder 100 caracteres")
    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    @Size(max = 18, message = "El CURP no puede exceder 18 caracteres")
    private String curp;

    // Persona Moral
    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    @Size(max = 200, message = "El nombre comercial no puede exceder 200 caracteres")
    private String nombreComercial;

    // Común
    @NotBlank(message = "El RFC es requerido")
    @Size(max = 13, message = "El RFC no puede exceder 13 caracteres")
    private String rfc;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Size(max = 20, message = "El teléfono móvil no puede exceder 20 caracteres")
    private String telefonoMovil;

    private List<Integer> rolesIds;
}
