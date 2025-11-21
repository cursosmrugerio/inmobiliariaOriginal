package com.inmobiliaria.mantenimiento.domain;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proveedores")
@Data
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

    private String rfc;

    @Column(name = "telefono_principal")
    private String telefonoPrincipal;

    @Column(name = "telefono_secundario")
    private String telefonoSecundario;

    private String email;

    private String direccion;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    private String ciudad;

    private String estado;

    @Column(name = "nombre_contacto")
    private String nombreContacto;

    @ElementCollection
    @CollectionTable(name = "proveedor_categorias", joinColumns = @JoinColumn(name = "proveedor_id"))
    @Column(name = "categoria")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<CategoriaMantenimiento> categorias = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String notas;

    private Boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
