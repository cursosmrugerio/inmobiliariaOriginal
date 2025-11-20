package com.inmobiliaria.propiedad;

import com.inmobiliaria.catalogo.*;
import com.inmobiliaria.shared.multitenancy.TenantAware;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propiedades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Propiedad implements TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_propiedad_id", nullable = false)
    private TipoPropiedad tipoPropiedad;

    // Identificación
    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "clave_catastral", length = 50)
    private String claveCatastral;

    // Dirección
    @Column(nullable = false, length = 200)
    private String calle;

    @Column(name = "numero_exterior", length = 20)
    private String numeroExterior;

    @Column(name = "numero_interior", length = 20)
    private String numeroInterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colonia_id")
    private Colonia colonia;

    @Column(name = "codigo_postal", length = 5)
    private String codigoPostal;

    private String referencias;

    // Características
    @Column(name = "superficie_terreno", precision = 10, scale = 2)
    private BigDecimal superficieTerreno;

    @Column(name = "superficie_construccion", precision = 10, scale = 2)
    private BigDecimal superficieConstruccion;

    @Column(name = "num_recamaras")
    private Integer numRecamaras;

    @Column(name = "num_banos", precision = 3, scale = 1)
    private BigDecimal numBanos;

    @Column(name = "num_estacionamientos")
    private Integer numEstacionamientos;

    @Column(name = "num_pisos")
    private Integer numPisos;

    @Column(name = "anio_construccion")
    private Integer anioConstruccion;

    // Valores
    @Column(name = "valor_comercial", precision = 15, scale = 2)
    private BigDecimal valorComercial;

    @Column(name = "valor_catastral", precision = 15, scale = 2)
    private BigDecimal valorCatastral;

    @Column(name = "renta_mensual", precision = 12, scale = 2)
    private BigDecimal rentaMensual;

    // Estado
    @Column(nullable = false)
    private boolean disponible = true;

    private String notas;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "propiedad", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropiedadPropietario> propietarios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(calle);
        if (numeroExterior != null) sb.append(" ").append(numeroExterior);
        if (numeroInterior != null) sb.append(" Int. ").append(numeroInterior);
        if (colonia != null) sb.append(", ").append(colonia.getNombre());
        if (municipio != null) sb.append(", ").append(municipio.getNombre());
        if (estado != null) sb.append(", ").append(estado.getNombre());
        if (codigoPostal != null) sb.append(" C.P. ").append(codigoPostal);
        return sb.toString();
    }
}
