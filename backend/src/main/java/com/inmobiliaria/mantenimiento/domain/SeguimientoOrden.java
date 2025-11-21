package com.inmobiliaria.mantenimiento.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_ordenes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoOrden estadoNuevo;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
