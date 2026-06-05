package com.sistemasdistr.basico.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pokemons_atrapados")
public class PokemonAtrapado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String especie; // Ej: Pikachu, Charizard

    private String apodo;
    
    private Integer nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    // Constructores
    public PokemonAtrapado() {}

    public PokemonAtrapado(String especie, String apodo, Integer nivel, Entrenador entrenador) {
        this.especie = especie;
        this.apodo = apodo;
        this.nivel = nivel;
        this.entrenador = entrenador;
    }
}