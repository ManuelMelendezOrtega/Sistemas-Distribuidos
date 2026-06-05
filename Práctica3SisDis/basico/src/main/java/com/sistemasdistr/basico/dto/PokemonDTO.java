package com.sistemasdistr.basico.dto;

import lombok.Data;

// Objeto de transferencia de datos (DTO).
// Sirve como molde temporal para almacenar la información del Pokémon que llega desde Python.
@Data
public class PokemonDTO {
    private String name;
    private int height;
    private int weight;
    private String type;
}