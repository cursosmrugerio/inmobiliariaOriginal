package com.inmobiliaria.propiedad;

import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "propiedad_propietario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadPropietario implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Persona propietario;

    @Column(name = "porcentaje_propiedad", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajePropiedad = new BigDecimal("100.00");

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    @Column(name = "es_principal")
    private boolean esPrincipal = false;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
