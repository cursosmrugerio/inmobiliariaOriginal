package com.inmobiliaria.propiedad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropiedadRequest {

    @NotNull(message = "El tipo de propiedad es requerido")
    private Integer tipoPropiedadId;

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private String claveCatastral;

    @NotBlank(message = "La calle es requerida")
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

    private String notas;

    // Propietarios iniciales
    private List<Long> propietariosIds;
}
