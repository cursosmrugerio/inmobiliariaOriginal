package com.inmobiliaria.pago;

import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cargo implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cargo", nullable = false)
    private TipoCargo tipoCargo;

    @Column(nullable = false, length = 200)
    private String concepto;

    @Column(name = "monto_original", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Column(name = "monto_pendiente", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPendiente;

    @Column(name = "fecha_cargo", nullable = false)
    private LocalDate fechaCargo;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoCargo estado = EstadoCargo.PENDIENTE;

    @Column(name = "es_cargo_fijo")
    @Builder.Default
    private Boolean esCargoFijo = false;

    @Column(name = "periodo_mes")
    private Integer periodoMes;

    @Column(name = "periodo_anio")
    private Integer periodoAnio;

    @Column(length = 500)
    private String notas;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (montoPendiente == null) {
            montoPendiente = montoOriginal;
        }
        if (montoPagado == null) {
            montoPagado = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void aplicarPago(BigDecimal monto) {
        this.montoPagado = this.montoPagado.add(monto);
        this.montoPendiente = this.montoOriginal.subtract(this.montoPagado);

        if (this.montoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            this.estado = EstadoCargo.PAGADO;
            this.montoPendiente = BigDecimal.ZERO;
        } else if (this.montoPagado.compareTo(BigDecimal.ZERO) > 0) {
            this.estado = EstadoCargo.PARCIAL;
        }
    }
}
