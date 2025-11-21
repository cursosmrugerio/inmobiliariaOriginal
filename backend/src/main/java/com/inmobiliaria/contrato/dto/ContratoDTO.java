package com.inmobiliaria.contrato.dto;

import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.contrato.EstadoContrato;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ContratoDTO {

    private Long id;
    private String numeroContrato;

    // Propiedad
    private Long propiedadId;
    private String propiedadNombre;
    private String propiedadDireccion;

    // Arrendatario
    private Long arrendatarioId;
    private String arrendatarioNombre;
    private String arrendatarioEmail;
    private String arrendatarioTelefono;

    // Aval
    private Long avalId;
    private String avalNombre;
    private String avalTelefono;

    // Fechas
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diaPago;

    // Montos
    private BigDecimal montoRenta;
    private BigDecimal montoDeposito;
    private BigDecimal montoPenalidadDiaria;
    private Integer diasGracia;
    private BigDecimal porcentajeIncrementoAnual;

    // Estado
    private EstadoContrato estado;
    private String condiciones;
    private String notas;
    private Long contratoAnteriorId;
    private boolean activo;

    // Calculados
    private long diasRestantes;
    private boolean vigente;
    private boolean porVencer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContratoDTO fromEntity(Contrato contrato) {
        ContratoDTOBuilder builder = ContratoDTO.builder()
                .id(contrato.getId())
                .numeroContrato(contrato.getNumeroContrato())
                .propiedadId(contrato.getPropiedad().getId())
                .propiedadNombre(contrato.getPropiedad().getNombre())
                .propiedadDireccion(contrato.getPropiedad().getDireccionCompleta())
                .arrendatarioId(contrato.getArrendatario().getId())
                .arrendatarioNombre(contrato.getArrendatario().getNombreCompleto())
                .arrendatarioEmail(contrato.getArrendatario().getEmail())
                .arrendatarioTelefono(contrato.getArrendatario().getTelefono())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFin(contrato.getFechaFin())
                .diaPago(contrato.getDiaPago())
                .montoRenta(contrato.getMontoRenta())
                .montoDeposito(contrato.getMontoDeposito())
                .montoPenalidadDiaria(contrato.getMontoPenalidadDiaria())
                .diasGracia(contrato.getDiasGracia())
                .porcentajeIncrementoAnual(contrato.getPorcentajeIncrementoAnual())
                .estado(contrato.getEstado())
                .condiciones(contrato.getCondiciones())
                .notas(contrato.getNotas())
                .contratoAnteriorId(contrato.getContratoAnteriorId())
                .activo(contrato.isActivo())
                .diasRestantes(contrato.getDiasRestantes())
                .vigente(contrato.isVigente())
                .porVencer(contrato.isPorVencer(30))
                .createdAt(contrato.getCreatedAt())
                .updatedAt(contrato.getUpdatedAt());

        if (contrato.getAval() != null) {
            builder.avalId(contrato.getAval().getId())
                   .avalNombre(contrato.getAval().getNombreCompleto())
                   .avalTelefono(contrato.getAval().getTelefono());
        }

        return builder.build();
    }

    public static ContratoDTO fromEntityBasic(Contrato contrato) {
        ContratoDTOBuilder builder = ContratoDTO.builder()
                .id(contrato.getId())
                .numeroContrato(contrato.getNumeroContrato())
                .propiedadId(contrato.getPropiedad().getId())
                .propiedadNombre(contrato.getPropiedad().getNombre())
                .arrendatarioId(contrato.getArrendatario().getId())
                .arrendatarioNombre(contrato.getArrendatario().getNombreCompleto())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFin(contrato.getFechaFin())
                .montoRenta(contrato.getMontoRenta())
                .estado(contrato.getEstado())
                .activo(contrato.isActivo())
                .diasRestantes(contrato.getDiasRestantes())
                .vigente(contrato.isVigente())
                .porVencer(contrato.isPorVencer(30));

        return builder.build();
    }
}
