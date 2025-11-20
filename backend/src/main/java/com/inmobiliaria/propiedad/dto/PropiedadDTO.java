package com.inmobiliaria.propiedad.dto;

import com.inmobiliaria.propiedad.Propiedad;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadDTO {

    private Long id;
    private Long empresaId;

    // Tipo
    private Integer tipoPropiedadId;
    private String tipoPropiedadNombre;

    // Identificación
    private String nombre;
    private String claveCatastral;

    // Dirección
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
    private String direccionCompleta;

    // Características
    private BigDecimal superficieTerreno;
    private BigDecimal superficieConstruccion;
    private Integer numRecamaras;
    private BigDecimal numBanos;
    private Integer numEstacionamientos;
    private Integer numPisos;
    private Integer anioConstruccion;

    // Valores
    private BigDecimal valorComercial;
    private BigDecimal valorCatastral;
    private BigDecimal rentaMensual;

    // Estado
    private boolean disponible;
    private String notas;
    private boolean activo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relaciones
    private List<PropiedadPropietarioDTO> propietarios;

    public static PropiedadDTO fromEntity(Propiedad propiedad) {
        PropiedadDTOBuilder builder = PropiedadDTO.builder()
                .id(propiedad.getId())
                .empresaId(propiedad.getEmpresaId())
                .nombre(propiedad.getNombre())
                .claveCatastral(propiedad.getClaveCatastral())
                .calle(propiedad.getCalle())
                .numeroExterior(propiedad.getNumeroExterior())
                .numeroInterior(propiedad.getNumeroInterior())
                .codigoPostal(propiedad.getCodigoPostal())
                .referencias(propiedad.getReferencias())
                .direccionCompleta(propiedad.getDireccionCompleta())
                .superficieTerreno(propiedad.getSuperficieTerreno())
                .superficieConstruccion(propiedad.getSuperficieConstruccion())
                .numRecamaras(propiedad.getNumRecamaras())
                .numBanos(propiedad.getNumBanos())
                .numEstacionamientos(propiedad.getNumEstacionamientos())
                .numPisos(propiedad.getNumPisos())
                .anioConstruccion(propiedad.getAnioConstruccion())
                .valorComercial(propiedad.getValorComercial())
                .valorCatastral(propiedad.getValorCatastral())
                .rentaMensual(propiedad.getRentaMensual())
                .disponible(propiedad.isDisponible())
                .notas(propiedad.getNotas())
                .activo(propiedad.isActivo())
                .createdAt(propiedad.getCreatedAt())
                .updatedAt(propiedad.getUpdatedAt());

        if (propiedad.getTipoPropiedad() != null) {
            builder.tipoPropiedadId(propiedad.getTipoPropiedad().getId())
                    .tipoPropiedadNombre(propiedad.getTipoPropiedad().getNombre());
        }

        if (propiedad.getEstado() != null) {
            builder.estadoId(propiedad.getEstado().getId())
                    .estadoNombre(propiedad.getEstado().getNombre());
        }

        if (propiedad.getMunicipio() != null) {
            builder.municipioId(propiedad.getMunicipio().getId())
                    .municipioNombre(propiedad.getMunicipio().getNombre());
        }

        if (propiedad.getColonia() != null) {
            builder.coloniaId(propiedad.getColonia().getId())
                    .coloniaNombre(propiedad.getColonia().getNombre());
        }

        if (propiedad.getPropietarios() != null) {
            builder.propietarios(propiedad.getPropietarios().stream()
                    .map(PropiedadPropietarioDTO::fromEntity)
                    .toList());
        }

        return builder.build();
    }

    public static PropiedadDTO fromEntityBasic(Propiedad propiedad) {
        PropiedadDTOBuilder builder = PropiedadDTO.builder()
                .id(propiedad.getId())
                .empresaId(propiedad.getEmpresaId())
                .nombre(propiedad.getNombre())
                .direccionCompleta(propiedad.getDireccionCompleta())
                .rentaMensual(propiedad.getRentaMensual())
                .disponible(propiedad.isDisponible())
                .activo(propiedad.isActivo());

        if (propiedad.getTipoPropiedad() != null) {
            builder.tipoPropiedadId(propiedad.getTipoPropiedad().getId())
                    .tipoPropiedadNombre(propiedad.getTipoPropiedad().getNombre());
        }

        return builder.build();
    }
}
