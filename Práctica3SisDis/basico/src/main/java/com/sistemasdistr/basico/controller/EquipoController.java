package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Equipo;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.EquipoRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// Controlador principal de los equipos.
// Atiende las peticiones web de los usuarios, valida con Python y guarda en base de datos.
@Controller
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoRepository equipoRepository;
    private final UserRepository userRepository;

    // Constructor: Conecta el controlador con las bases de datos necesarias
    public EquipoController(EquipoRepository equipoRepository, UserRepository userRepository) {
        this.equipoRepository = equipoRepository;
        this.userRepository = userRepository;
    }

    // Carga la lista de todos los equipos del usuario que ha iniciado sesión
    @GetMapping
    public String listarEquipos(Model model, Principal principal) {
        User usuarioActual = userRepository.findUserByUsername(principal.getName());
        List<Equipo> misEquipos = equipoRepository.findByUsuario(usuarioActual);
        if (misEquipos == null) misEquipos = new ArrayList<>();
        
        model.addAttribute("equipos", misEquipos);
        return "equipos/lista";
    }

    // Muestra el formulario en blanco para crear un equipo desde cero
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("equipo")) {
            model.addAttribute("equipo", new Equipo());
        }
        return "equipos/formulario";
    }

    // Recibe los datos del formulario, los valida con Python y, si son correctos, los guarda
    @PostMapping("/guardar")
    public String guardarEquipo(@ModelAttribute Equipo equipo, Principal principal, Model model) {
        
        RestTemplate restTemplate = new RestTemplate();
        String[] pokemonDelEquipo = {equipo.getP1(), equipo.getP2(), equipo.getP3(), equipo.getP4(), equipo.getP5(), equipo.getP6()};
        
        for (String poke : pokemonDelEquipo) {
            
            if (poke == null || poke.trim().isEmpty()) continue;

            try {
                // Limpiamos el texto antes de enviarlo a Python
                String nombreLimpio = poke.trim().toLowerCase().replace(" ", "-");
                String urlPython = "http://localhost:5000/pokemon/" + nombreLimpio;
                restTemplate.getForObject(urlPython, String.class);
                
            } catch (HttpStatusCodeException e) {
                // Python responde que el Pokémon no existe
                model.addAttribute("errorPokemon", "El Pokémon '" + poke + "' no existe. Escríbelo bien en inglés.");
                model.addAttribute("equipo", equipo); 
                return "equipos/formulario"; 
                
            } catch (Exception e) {
                // El microservicio de Python está apagado o fallando
                model.addAttribute("errorPokemon", "Error de conexión: El microservicio de Python no está respondiendo.");
                model.addAttribute("equipo", equipo);
                return "equipos/formulario"; 
            }
        }

        // Si la validación es un éxito, asignamos el dueño y guardamos en MySQL
        User usuarioActual = userRepository.findUserByUsername(principal.getName());
        equipo.setUsuario(usuarioActual); 
        equipoRepository.save(equipo);
        
        return "redirect:/equipos";
    }

    // Busca un equipo existente para que su dueño pueda modificarlo
    @GetMapping("/editar/{id}")
    public String editarEquipo(@PathVariable Integer id, Model model, Principal principal) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        
        // Medida de seguridad: Comprobamos que no esté intentando editar el equipo de otra persona
        if (equipo != null && equipo.getUsuario().getUsername().equals(principal.getName())) {
            model.addAttribute("equipo", equipo);
            return "equipos/formulario";
        }
        return "redirect:/equipos";
    }

    // Borra un equipo específico, asegurándose primero de que le pertenece al usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarEquipo(@PathVariable Integer id, Principal principal) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        
        // Medida de seguridad: Comprobamos que sea el dueño legítimo antes de borrar
        if (equipo != null && equipo.getUsuario().getUsername().equals(principal.getName())) {
            equipoRepository.deleteById(id);
        }
        return "redirect:/equipos";
    }
}