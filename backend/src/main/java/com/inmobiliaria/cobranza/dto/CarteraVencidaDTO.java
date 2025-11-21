package com.inmobiliaria.cobranza.dto;

import com.inmobiliaria.cobranza.domain.CarteraVencida;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CarteraVencidaDTO {
    private Long id;
    private Long empresaId;
    private Long contratoId;
    private Long personaId;
    private Long propiedadId;
    private BigDecimal montoOriginal;
    private BigDecimal montoPendiente;
    private BigDecimal montoPenalidad;
    private LocalDate fechaVencimiento;
    private Integer diasVencido;
    private String concepto;
    private String estadoCobranza;
    private String clasificacionAntiguedad;
    private BigDecimal porcentajePenalidad;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional display fields
    private String nombrePersona;
    private String direccionPropiedad;
    private BigDecimal montoTotal;

    public static CarteraVencidaDTO fromEntity(CarteraVencida entity) {
        BigDecimal total = entity.getMontoPendiente();
        if (entity.getMontoPenalidad() != null) {
            total = total.add(entity.getMontoPenalidad());
        }

        return CarteraVencidaDTO.builder()
                .id(entity.getId())
                .empresaId(entity.getEmpresaId())
                .contratoId(entity.getContratoId())
                .personaId(entity.getPersonaId())
                .propiedadId(entity.getPropiedadId())
                .montoOriginal(entity.getMontoOriginal())
                .montoPendiente(entity.getMontoPendiente())
                .montoPenalidad(entity.getMontoPenalidad())
                .fechaVencimiento(entity.getFechaVencimiento())
                .diasVencido(entity.getDiasVencido())
                .concepto(entity.getConcepto())
                .estadoCobranza(entity.getEstadoCobranza() != null ? entity.getEstadoCobranza().name() : null)
                .clasificacionAntiguedad(entity.getClasificacionAntiguedad() != null ? entity.getClasificacionAntiguedad().name() : null)
                .porcentajePenalidad(entity.getPorcentajePenalidad())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .montoTotal(total)
                .build();
    }
}
