package com.inmobiliaria.propiedad.dto;

import com.inmobiliaria.propiedad.PropiedadPropietario;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadPropietarioDTO {

    private Long id;
    private Long propiedadId;
    private Long propietarioId;
    private String propietarioNombre;
    private String propietarioRfc;
    private BigDecimal porcentajePropiedad;
    private LocalDate fechaAdquisicion;
    private boolean esPrincipal;
    private boolean activo;

    public static PropiedadPropietarioDTO fromEntity(PropiedadPropietario pp) {
        return PropiedadPropietarioDTO.builder()
                .id(pp.getId())
                .propiedadId(pp.getPropiedad().getId())
                .propietarioId(pp.getPropietario().getId())
                .propietarioNombre(pp.getPropietario().getNombreCompleto())
                .propietarioRfc(pp.getPropietario().getRfc())
                .porcentajePropiedad(pp.getPorcentajePropiedad())
                .fechaAdquisicion(pp.getFechaAdquisicion())
                .esPrincipal(pp.isEsPrincipal())
                .activo(pp.isActivo())
                .build();
    }
}
