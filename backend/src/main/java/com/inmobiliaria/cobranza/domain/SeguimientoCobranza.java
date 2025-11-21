package com.inmobiliaria.cobranza.domain;

import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_cobranza")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoCobranza implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cartera_vencida_id", nullable = false)
    private Long carteraVencidaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contacto", length = 50, nullable = false)
    private TipoContacto tipoContacto;

    @Column(name = "fecha_contacto", nullable = false)
    private LocalDateTime fechaContacto;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", length = 50)
    private ResultadoContacto resultado;

    @Column(name = "fecha_promesa_pago")
    private LocalDate fechaPromesaPago;

    @Column(name = "monto_promesa", precision = 12, scale = 2)
    private BigDecimal montoPromesa;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "proxima_accion", length = 500)
    private String proximaAccion;

    @Column(name = "fecha_proxima_accion")
    private LocalDate fechaProximaAccion;

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
        if (this.fechaContacto == null) {
            this.fechaContacto = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TipoContacto {
        LLAMADA_TELEFONICA,
        WHATSAPP,
        EMAIL,
        VISITA_DOMICILIO,
        CARTA_COBRANZA,
        NOTIFICACION_LEGAL
    }

    public enum ResultadoContacto {
        CONTACTADO_PROMESA_PAGO,
        CONTACTADO_SIN_COMPROMISO,
        NO_CONTACTADO,
        NUMERO_EQUIVOCADO,
        BUZON_VOZ,
        PAGO_REALIZADO
    }
}
