package com.inmobiliaria.catalogo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_estados")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 2)
    private String clave;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;
}
