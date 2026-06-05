package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.PokemonAtrapado;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.PokemonAtrapadoRepository;
import com.sistemasdistr.basico.repository.EntrenadorRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/pokemons")
public class PokemonAtrapadoController {

    @Autowired
    private PokemonAtrapadoRepository pokemonRepository;

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Listar solo los Pokémon de los entrenadores del usuario logueado
    @GetMapping
    public String listarPokemons(Model model, Principal principal) {
        User miUsuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Asegúrate de usar esta llamada exacta en el @GetMapping:
        model.addAttribute("pokemons", pokemonRepository.findByEntrenadorUsuario(miUsuario));
        return "lista_pokemons";
    }

    // 2. Mostrar formulario con solo MIS entrenadores en el desplegable
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Principal principal) {
        User miUsuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("pokemon", new PokemonAtrapado());
        // Filtramos el desplegable: solo salen entrenadores que son míos
        model.addAttribute("entrenadores", entrenadorRepository.findByUsuario(miUsuario));
        return "formulario_pokemons";
    }

    // 3. Guardar el Pokémon
    @PostMapping
    public String guardarPokemon(@ModelAttribute PokemonAtrapado pokemon) {
        pokemonRepository.save(pokemon);
        return "redirect:/pokemons";
    }

    // 4. Eliminar
    @GetMapping("/{id}/eliminar")
    public String eliminarPokemon(@PathVariable Long id) {
        pokemonRepository.deleteById(id);
        return "redirect:/pokemons";
    }
}