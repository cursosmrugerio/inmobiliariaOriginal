package com.inmobiliaria.cobranza.dto;

import com.inmobiliaria.cobranza.domain.SeguimientoCobranza;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SeguimientoCobranzaDTO {
    private Long id;
    private Long empresaId;
    private Long carteraVencidaId;
    private String tipoContacto;
    private LocalDateTime fechaContacto;
    private String descripcion;
    private String resultado;
    private LocalDate fechaPromesaPago;
    private BigDecimal montoPromesa;
    private Long usuarioId;
    private String proximaAccion;
    private LocalDate fechaProximaAccion;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional display fields
    private String nombreUsuario;

    public static SeguimientoCobranzaDTO fromEntity(SeguimientoCobranza entity) {
        return SeguimientoCobranzaDTO.builder()
                .id(entity.getId())
                .empresaId(entity.getEmpresaId())
                .carteraVencidaId(entity.getCarteraVencidaId())
                .tipoContacto(entity.getTipoContacto() != null ? entity.getTipoContacto().name() : null)
                .fechaContacto(entity.getFechaContacto())
                .descripcion(entity.getDescripcion())
                .resultado(entity.getResultado() != null ? entity.getResultado().name() : null)
                .fechaPromesaPago(entity.getFechaPromesaPago())
                .montoPromesa(entity.getMontoPromesa())
                .usuarioId(entity.getUsuarioId())
                .proximaAccion(entity.getProximaAccion())
                .fechaProximaAccion(entity.getFechaProximaAccion())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
