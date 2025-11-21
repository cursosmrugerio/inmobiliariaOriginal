package com.inmobiliaria.cobranza.domain;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "proyeccion_cobranza")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProyeccionCobranza implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "periodo", nullable = false)
    private LocalDate periodo;

    @Column(name = "monto_proyectado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoProyectado;

    @Column(name = "monto_cobrado", precision = 14, scale = 2)
    private BigDecimal montoCobrado;

    @Column(name = "monto_pendiente", precision = 14, scale = 2)
    private BigDecimal montoPendiente;

    @Column(name = "cantidad_contratos")
    private Integer cantidadContratos;

    @Column(name = "cantidad_pagos_esperados")
    private Integer cantidadPagosEsperados;

    @Column(name = "cantidad_pagos_recibidos")
    private Integer cantidadPagosRecibidos;

    @Column(name = "porcentaje_cumplimiento", precision = 5, scale = 2)
    private BigDecimal porcentajeCumplimiento;

    @Column(name = "notas", length = 1000)
    private String notas;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.montoCobrado == null) {
            this.montoCobrado = BigDecimal.ZERO;
        }
        if (this.cantidadPagosRecibidos == null) {
            this.cantidadPagosRecibidos = 0;
        }
        calcularPendienteYCumplimiento();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calcularPendienteYCumplimiento();
    }

    private void calcularPendienteYCumplimiento() {
        if (this.montoProyectado != null && this.montoCobrado != null) {
            this.montoPendiente = this.montoProyectado.subtract(this.montoCobrado);
            if (this.montoProyectado.compareTo(BigDecimal.ZERO) > 0) {
                this.porcentajeCumplimiento = this.montoCobrado
                    .multiply(BigDecimal.valueOf(100))
                    .divide(this.montoProyectado, 2, java.math.RoundingMode.HALF_UP);
            }
        }
    }
}
