package com.inmobiliaria.mantenimiento.dto;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.Proveedor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    private Long id;
    private Long empresaId;
    private String nombre;
    private String razonSocial;
    private String rfc;
    private String telefonoPrincipal;
    private String telefonoSecundario;
    private String email;
    private String direccion;
    private String codigoPostal;
    private String ciudad;
    private String estado;
    private String nombreContacto;
    private Set<CategoriaMantenimiento> categorias;
    private String notas;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static ProveedorDTO fromEntity(Proveedor proveedor) {
        return ProveedorDTO.builder()
                .id(proveedor.getId())
                .empresaId(proveedor.getEmpresaId())
                .nombre(proveedor.getNombre())
                .razonSocial(proveedor.getRazonSocial())
                .rfc(proveedor.getRfc())
                .telefonoPrincipal(proveedor.getTelefonoPrincipal())
                .telefonoSecundario(proveedor.getTelefonoSecundario())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .codigoPostal(proveedor.getCodigoPostal())
                .ciudad(proveedor.getCiudad())
                .estado(proveedor.getEstado())
                .nombreContacto(proveedor.getNombreContacto())
                .categorias(proveedor.getCategorias())
                .notas(proveedor.getNotas())
                .activo(proveedor.getActivo())
                .fechaCreacion(proveedor.getFechaCreacion())
                .fechaActualizacion(proveedor.getFechaActualizacion())
                .build();
    }
}
