package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.TipoPersona;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PersonaDTO {
    private Long id;
    private Long empresaId;
    private TipoPersona tipoPersona;

    // Persona Física
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String curp;

    // Persona Moral
    private String razonSocial;
    private String nombreComercial;

    // Común
    private String rfc;
    private String email;
    private String telefono;
    private String telefonoMovil;
    private String nombreCompleto;

    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PersonaRolDTO> roles;
    private List<DireccionDTO> direcciones;
    private List<CuentaBancariaDTO> cuentasBancarias;

    public static PersonaDTO fromEntity(Persona persona) {
        return PersonaDTO.builder()
                .id(persona.getId())
                .empresaId(persona.getEmpresaId())
                .tipoPersona(persona.getTipoPersona())
                .nombre(persona.getNombre())
                .apellidoPaterno(persona.getApellidoPaterno())
                .apellidoMaterno(persona.getApellidoMaterno())
                .fechaNacimiento(persona.getFechaNacimiento())
                .curp(persona.getCurp())
                .razonSocial(persona.getRazonSocial())
                .nombreComercial(persona.getNombreComercial())
                .rfc(persona.getRfc())
                .email(persona.getEmail())
                .telefono(persona.getTelefono())
                .telefonoMovil(persona.getTelefonoMovil())
                .nombreCompleto(persona.getNombreCompleto())
                .activo(persona.isActivo())
                .createdAt(persona.getCreatedAt())
                .updatedAt(persona.getUpdatedAt())
                .roles(persona.getRoles().stream().map(PersonaRolDTO::fromEntity).toList())
                .direcciones(persona.getDirecciones().stream().map(DireccionDTO::fromEntity).toList())
                .cuentasBancarias(persona.getCuentasBancarias().stream().map(CuentaBancariaDTO::fromEntity).toList())
                .build();
    }

    public static PersonaDTO fromEntityBasic(Persona persona) {
        return PersonaDTO.builder()
                .id(persona.getId())
                .empresaId(persona.getEmpresaId())
                .tipoPersona(persona.getTipoPersona())
                .nombre(persona.getNombre())
                .apellidoPaterno(persona.getApellidoPaterno())
                .apellidoMaterno(persona.getApellidoMaterno())
                .razonSocial(persona.getRazonSocial())
                .rfc(persona.getRfc())
                .email(persona.getEmail())
                .telefono(persona.getTelefono())
                .nombreCompleto(persona.getNombreCompleto())
                .activo(persona.isActivo())
                .build();
    }
}
