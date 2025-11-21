package com.inmobiliaria.cobranza.dto;

import com.inmobiliaria.cobranza.domain.ProyeccionCobranza;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProyeccionCobranzaDTO {
    private Long id;
    private Long empresaId;
    private LocalDate periodo;
    private BigDecimal montoProyectado;
    private BigDecimal montoCobrado;
    private BigDecimal montoPendiente;
    private Integer cantidadContratos;
    private Integer cantidadPagosEsperados;
    private Integer cantidadPagosRecibidos;
    private BigDecimal porcentajeCumplimiento;
    private String notas;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProyeccionCobranzaDTO fromEntity(ProyeccionCobranza entity) {
        return ProyeccionCobranzaDTO.builder()
                .id(entity.getId())
                .empresaId(entity.getEmpresaId())
                .periodo(entity.getPeriodo())
                .montoProyectado(entity.getMontoProyectado())
                .montoCobrado(entity.getMontoCobrado())
                .montoPendiente(entity.getMontoPendiente())
                .cantidadContratos(entity.getCantidadContratos())
                .cantidadPagosEsperados(entity.getCantidadPagosEsperados())
                .cantidadPagosRecibidos(entity.getCantidadPagosRecibidos())
                .porcentajeCumplimiento(entity.getPorcentajeCumplimiento())
                .notas(entity.getNotas())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
