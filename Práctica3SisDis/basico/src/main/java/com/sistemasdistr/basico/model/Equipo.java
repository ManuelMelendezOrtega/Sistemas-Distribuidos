package com.sistemasdistr.basico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Modelo que representa la tabla 'equipo' en la base de datos.
// Guarda el nombre del equipo, los 6 Pokémon que lo forman y se enlaza con el usuario creador.
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombreEquipo;

    @Column(nullable = false, length = 50) private String p1;
    @Column(nullable = false, length = 50) private String p2;
    @Column(nullable = false, length = 50) private String p3;
    @Column(nullable = false, length = 50) private String p4;
    @Column(nullable = false, length = 50) private String p5;
    @Column(nullable = false, length = 50) private String p6;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;
}