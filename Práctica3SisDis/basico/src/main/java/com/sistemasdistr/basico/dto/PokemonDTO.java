package com.sistemasdistr.basico.dto;

import lombok.Data;

// Plantilla sencilla para guardar los datos del Pokémon que recibimos desde Python.
// Solo sirve para transportar la información (nombre, peso, foto...) hacia la página web.
@Data
public class PokemonDTO {
    private String name;
    private int height;
    private int weight;
    private String type;
    private String image;       
    private String description; 
}