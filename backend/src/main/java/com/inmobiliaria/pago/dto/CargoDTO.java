package com.inmobiliaria.pago.dto;

import com.inmobiliaria.pago.EstadoCargo;
import com.inmobiliaria.pago.TipoCargo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CargoDTO {
    private Long id;
    private Long contratoId;
    private String numeroContrato;
    private String propiedadDireccion;
    private String arrendatarioNombre;
    private TipoCargo tipoCargo;
    private String concepto;
    private BigDecimal montoOriginal;
    private BigDecimal montoPagado;
    private BigDecimal montoPendiente;
    private LocalDate fechaCargo;
    private LocalDate fechaVencimiento;
    private EstadoCargo estado;
    private Boolean esCargoFijo;
    private Integer periodoMes;
    private Integer periodoAnio;
    private String notas;
    private LocalDateTime createdAt;
}
