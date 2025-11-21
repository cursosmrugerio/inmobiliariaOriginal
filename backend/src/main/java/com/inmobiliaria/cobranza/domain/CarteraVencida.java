package com.inmobiliaria.cobranza.domain;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cartera_vencida")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarteraVencida implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "contrato_id", nullable = false)
    private Long contratoId;

    @Column(name = "persona_id", nullable = false)
    private Long personaId;

    @Column(name = "propiedad_id", nullable = false)
    private Long propiedadId;

    @Column(name = "monto_original", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "monto_pendiente", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPendiente;

    @Column(name = "monto_penalidad", precision = 12, scale = 2)
    private BigDecimal montoPenalidad;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "dias_vencido")
    private Integer diasVencido;

    @Column(name = "concepto", length = 500)
    private String concepto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cobranza", length = 50)
    private EstadoCobranza estadoCobranza;

    @Enumerated(EnumType.STRING)
    @Column(name = "clasificacion_antiguedad", length = 50)
    private ClasificacionAntiguedad clasificacionAntiguedad;

    @Column(name = "porcentaje_penalidad", precision = 5, scale = 2)
    private BigDecimal porcentajePenalidad;

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
        if (this.estadoCobranza == null) {
            this.estadoCobranza = EstadoCobranza.PENDIENTE;
        }
        calcularDiasVencido();
        calcularClasificacion();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calcularDiasVencido();
        calcularClasificacion();
    }

    private void calcularDiasVencido() {
        if (this.fechaVencimiento != null) {
            this.diasVencido = (int) java.time.temporal.ChronoUnit.DAYS.between(
                this.fechaVencimiento, LocalDate.now());
            if (this.diasVencido < 0) {
                this.diasVencido = 0;
            }
        }
    }

    private void calcularClasificacion() {
        if (this.diasVencido == null || this.diasVencido <= 0) {
            this.clasificacionAntiguedad = ClasificacionAntiguedad.VIGENTE;
        } else if (this.diasVencido <= 30) {
            this.clasificacionAntiguedad = ClasificacionAntiguedad.VENCIDO_1_30;
        } else if (this.diasVencido <= 60) {
            this.clasificacionAntiguedad = ClasificacionAntiguedad.VENCIDO_31_60;
        } else if (this.diasVencido <= 90) {
            this.clasificacionAntiguedad = ClasificacionAntiguedad.VENCIDO_61_90;
        } else {
            this.clasificacionAntiguedad = ClasificacionAntiguedad.VENCIDO_MAS_90;
        }
    }

    public enum EstadoCobranza {
        PENDIENTE,
        EN_GESTION,
        PROMESA_PAGO,
        PARCIALMENTE_PAGADO,
        PAGADO,
        INCOBRABLE
    }

    public enum ClasificacionAntiguedad {
        VIGENTE,
        VENCIDO_1_30,
        VENCIDO_31_60,
        VENCIDO_61_90,
        VENCIDO_MAS_90
    }
}
