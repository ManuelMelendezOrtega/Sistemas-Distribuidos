package com.sistemasdistr.basico.dto;

import lombok.Data;

@Data
public class PokemonDTO {
    private String name;
    private int height;
    private int weight;
    private String type;
    private String image;       // Nuevo campo para la foto
    private String description; // Nuevo campo para la descripción
}