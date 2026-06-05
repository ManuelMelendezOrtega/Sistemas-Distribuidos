package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.dto.PokemonDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

// Controlador principal de la aplicación web. 
// Se encarga de recibir las peticiones del navegador y decidir qué pantalla de Thymeleaf mostrar.
@Controller
public class maincontroller {

    // Muestra la pantalla principal pública de bienvenida.
    @GetMapping("/")
    public String paginaPrincipal() {
        return "index"; 
    }

    // Muestra la pantalla del buscador de Pokémon (requiere haber iniciado sesión).
    @GetMapping("/buscador")
    public String mostrarBuscador() {
        return "buscador"; 
    }

    // Recoge el nombre del Pokémon buscado, hace la petición a Python y procesa los posibles errores.
    @GetMapping("/pokemon/buscar")
    public String mostrarResultado(@RequestParam String nombre, ModelMap model) {
        String url = "http://localhost:5000/pokemon/" + nombre;
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            PokemonDTO pokemon = restTemplate.getForObject(url, PokemonDTO.class);
            model.addAttribute("pokemon", pokemon);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String errorJson = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode nodo = mapper.readTree(errorJson);
                String mensajeLimpio = nodo.path("error").asString();
                model.addAttribute("error", mensajeLimpio);
            } catch (Exception ex) {
                model.addAttribute("error", "Error en el Microservicio: " + errorJson);
            }
        } catch (Exception e) {
            model.addAttribute("error", "El sistema no está disponible actualmente.");
        }
        return "resultado_pokemon";
    }
}