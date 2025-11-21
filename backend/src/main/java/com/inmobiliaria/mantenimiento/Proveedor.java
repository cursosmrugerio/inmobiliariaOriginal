package com.inmobiliaria.mantenimiento;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(length = 13)
    private String rfc;

    private String especialidad;

    private String telefono;

    private String email;

    private String direccion;

    @Column(length = 500)
    private String notas;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
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
}
