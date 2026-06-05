package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Entrenador;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.EntrenadorRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/entrenadores")
public class EntrenadorController {

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Listar solo los entrenadores del usuario logueado
    @GetMapping
    public String listarEntrenadores(Model model, Principal principal) {
        User miUsuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("entrenadores", entrenadorRepository.findByUsuario(miUsuario));
        return "lista_entrenadores";
    }

    // 2. Mostrar formulario para crear uno nuevo
    @GetMapping("/nuevo")
    public String mostrarFormularioDeCreacion(Model model) {
        model.addAttribute("entrenador", new Entrenador());
        return "formulario_entrenadores";
    }

    // 3. Guardar el entrenador asignándolo al usuario actual
    @PostMapping
    public String guardarEntrenador(@ModelAttribute Entrenador entrenador, Principal principal) {
        User miUsuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Vinculamos el entrenador al usuario antes de guardar
        entrenador.setUsuario(miUsuario);
        entrenadorRepository.save(entrenador);
        
        return "redirect:/entrenadores";
    }
    
    // 4. Eliminar un entrenador
    @GetMapping("/{id}/eliminar")
    public String eliminarEntrenador(@PathVariable Long id) {
        entrenadorRepository.deleteById(id);
        return "redirect:/entrenadores";
    }
}