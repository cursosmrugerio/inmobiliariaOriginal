package com.inmobiliaria.propiedad.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePropiedadRequest {

    private Integer tipoPropiedadId;
    private String nombre;
    private String claveCatastral;

    // Dirección
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private Integer estadoId;
    private Integer municipioId;
    private Integer coloniaId;
    private String codigoPostal;
    private String referencias;

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
    private Boolean disponible;
    private String notas;
    private Boolean activo;
}
