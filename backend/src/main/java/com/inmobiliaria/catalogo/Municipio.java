package com.inmobiliaria.catalogo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_municipios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    @Column(nullable = false, length = 5)
    private String clave;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;
}
