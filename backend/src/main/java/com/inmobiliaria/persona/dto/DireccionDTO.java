package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.Direccion;
import com.inmobiliaria.persona.TipoDireccion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DireccionDTO {
    private Long id;
    private Long personaId;
    private TipoDireccion tipoDireccion;
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private Integer estadoId;
    private String estadoNombre;
    private Integer municipioId;
    private String municipioNombre;
    private Integer coloniaId;
    private String coloniaNombre;
    private String codigoPostal;
    private String referencias;
    private boolean esPrincipal;
    private boolean activo;

    public static DireccionDTO fromEntity(Direccion direccion) {
        return DireccionDTO.builder()
                .id(direccion.getId())
                .personaId(direccion.getPersona().getId())
                .tipoDireccion(direccion.getTipoDireccion())
                .calle(direccion.getCalle())
                .numeroExterior(direccion.getNumeroExterior())
                .numeroInterior(direccion.getNumeroInterior())
                .estadoId(direccion.getEstado() != null ? direccion.getEstado().getId() : null)
                .estadoNombre(direccion.getEstado() != null ? direccion.getEstado().getNombre() : null)
                .municipioId(direccion.getMunicipio() != null ? direccion.getMunicipio().getId() : null)
                .municipioNombre(direccion.getMunicipio() != null ? direccion.getMunicipio().getNombre() : null)
                .coloniaId(direccion.getColonia() != null ? direccion.getColonia().getId() : null)
                .coloniaNombre(direccion.getColonia() != null ? direccion.getColonia().getNombre() : null)
                .codigoPostal(direccion.getCodigoPostal())
                .referencias(direccion.getReferencias())
                .esPrincipal(direccion.isEsPrincipal())
                .activo(direccion.isActivo())
                .build();
    }
}
