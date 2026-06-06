package com.sistemasdistr.basico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inscripcion")
public class Inscripcion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    // Los 6 Pokémon del equipo elegido
    @Column(length = 50) private String pokemon1;
    @Column(length = 50) private String pokemon2;
    @Column(length = 50) private String pokemon3;
    @Column(length = 50) private String pokemon4;
    @Column(length = 50) private String pokemon5;
    @Column(length = 50) private String pokemon6;
}