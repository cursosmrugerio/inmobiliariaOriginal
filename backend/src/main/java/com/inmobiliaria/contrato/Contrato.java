package com.inmobiliaria.contrato;

import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contrato implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "numero_contrato", nullable = false, length = 50)
    private String numeroContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrendatario_id", nullable = false)
    private Persona arrendatario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aval_id")
    private Persona aval;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "dia_pago", nullable = false)
    private Integer diaPago;

    @Column(name = "monto_renta", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoRenta;

    @Column(name = "monto_deposito", precision = 12, scale = 2)
    private BigDecimal montoDeposito;

    @Column(name = "monto_fianza", precision = 12, scale = 2)
    private BigDecimal montoFianza;

    @Column(name = "monto_penalidad_diaria", precision = 10, scale = 2)
    private BigDecimal montoPenalidadDiaria;

    @Column(name = "dias_gracia")
    private Integer diasGracia;

    @Column(name = "porcentaje_incremento_anual", precision = 5, scale = 2)
    private BigDecimal porcentajeIncrementoAnual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoContrato estado = EstadoContrato.BORRADOR;

    @Column(columnDefinition = "TEXT")
    private String condiciones;

    private String notas;

    @Column(name = "contrato_anterior_id")
    private Long contratoAnteriorId;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        return estado == EstadoContrato.ACTIVO &&
               !hoy.isBefore(fechaInicio) &&
               !hoy.isAfter(fechaFin);
    }

    public boolean isPorVencer(int diasAnticipacion) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaAlerta = fechaFin.minusDays(diasAnticipacion);
        return estado == EstadoContrato.ACTIVO &&
               !hoy.isBefore(fechaAlerta) &&
               !hoy.isAfter(fechaFin);
    }

    public long getDiasRestantes() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
    }
}
