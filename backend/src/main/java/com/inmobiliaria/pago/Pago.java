package com.inmobiliaria.pago;

import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(name = "numero_recibo", length = 50)
    private String numeroRecibo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "monto_aplicado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoAplicado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "fecha_aplicacion")
    private LocalDate fechaAplicacion;

    @Column(length = 100)
    private String referencia;

    @Column(length = 100)
    private String banco;

    @Column(name = "numero_cheque", length = 50)
    private String numeroCheque;

    @Column(length = 500)
    private String notas;

    @Column(name = "comprobante_url", length = 500)
    private String comprobanteUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (montoAplicado == null) {
            montoAplicado = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getMontoDisponible() {
        return monto.subtract(montoAplicado);
    }

    public void aplicarMonto(BigDecimal montoAAplicar) {
        this.montoAplicado = this.montoAplicado.add(montoAAplicar);
        if (this.montoAplicado.compareTo(this.monto) >= 0) {
            this.estado = EstadoPago.APLICADO;
        } else if (this.montoAplicado.compareTo(BigDecimal.ZERO) > 0) {
            this.estado = EstadoPago.PARCIAL;
        }
    }
}
