package com.inmobiliaria.notificacion.domain;

import com.inmobiliaria.shared.tenant.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_notificaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNotificacion implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaNotificacion categoria;

    @Column(name = "email_habilitado")
    private Boolean emailHabilitado;

    @Column(name = "whatsapp_habilitado")
    private Boolean whatsappHabilitado;

    @Column(name = "dias_anticipacion")
    private Integer diasAnticipacion;

    @Column(name = "frecuencia_recordatorio")
    private Integer frecuenciaRecordatorio;

    @Column(name = "max_intentos")
    private Integer maxIntentos;

    @Column(name = "plantilla_email", columnDefinition = "TEXT")
    private String plantillaEmail;

    @Column(name = "plantilla_whatsapp", columnDefinition = "TEXT")
    private String plantillaWhatsapp;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (emailHabilitado == null) {
            emailHabilitado = true;
        }
        if (whatsappHabilitado == null) {
            whatsappHabilitado = false;
        }
        if (maxIntentos == null) {
            maxIntentos = 3;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
