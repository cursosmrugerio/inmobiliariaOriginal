package com.inmobiliaria.catalogo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_tipos_asentamiento")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoAsentamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;
}
