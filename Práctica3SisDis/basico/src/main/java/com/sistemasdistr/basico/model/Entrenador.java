package com.sistemasdistr.basico.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "entrenadores")
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String region;

    private Integer edad;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
    // -------------------------------------------------------

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL)
    private List<PokemonAtrapado> pokemons;

    // Constructores
    public Entrenador() {}

    public Entrenador(String nombre, String region, Integer edad) {
        this.nombre = nombre;
        this.region = region;
        this.edad = edad;
    }

}