package com.inmobiliaria.mantenimiento.domain;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_mantenimiento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenMantenimiento implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "numero_orden", unique = true)
    private String numeroOrden;

    @Column(name = "propiedad_id", nullable = false)
    private Long propiedadId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "solicitante_id")
    private Long solicitanteId;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaMantenimiento categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadOrden prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_completada")
    private LocalDate fechaCompletada;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "notas_tecnicas", columnDefinition = "TEXT")
    private String notasTecnicas;

    @Column(name = "notas_cierre", columnDefinition = "TEXT")
    private String notasCierre;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "creado_por")
    private String creadoPor;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoOrden.PENDIENTE;
        }
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDate.now();
        }
        if (numeroOrden == null) {
            numeroOrden = "OM-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
