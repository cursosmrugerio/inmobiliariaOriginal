package com.inmobiliaria.catalogo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_colonias")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Colonia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_asentamiento_id")
    private TipoAsentamiento tipoAsentamiento;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "codigo_postal", length = 5)
    private String codigoPostal;

    @Column(nullable = false)
    private boolean activo = true;
}
