package com.inmobiliaria.notificacion.domain;

import com.inmobiliaria.shared.tenant.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "persona_id")
    private Long personaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaNotificacion categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String asunto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "referencia_tipo")
    private String referenciaTipo;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "intentos")
    private Integer intentos;

    @Column(name = "error_mensaje")
    private String errorMensaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (intentos == null) {
            intentos = 0;
        }
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
