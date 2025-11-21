package com.inmobiliaria.pago.dto;

import com.inmobiliaria.pago.EstadoPago;
import com.inmobiliaria.pago.TipoPago;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PagoDTO {
    private Long id;
    private Long contratoId;
    private String numeroContrato;
    private Long personaId;
    private String personaNombre;
    private String propiedadDireccion;
    private String numeroRecibo;
    private BigDecimal monto;
    private BigDecimal montoAplicado;
    private BigDecimal montoDisponible;
    private TipoPago tipoPago;
    private EstadoPago estado;
    private LocalDate fechaPago;
    private LocalDate fechaAplicacion;
    private String referencia;
    private String banco;
    private String numeroCheque;
    private String notas;
    private String comprobanteUrl;
    private List<PagoAplicacionDTO> aplicaciones;
    private LocalDateTime createdAt;
}
