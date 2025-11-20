package com.inmobiliaria.catalogo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_codigos_postales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodigoPostal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 5)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colonia_id")
    private Colonia colonia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @Column(nullable = false)
    private boolean activo = true;
}
