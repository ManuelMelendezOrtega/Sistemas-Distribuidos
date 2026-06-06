package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Inscripcion;
import com.sistemasdistr.basico.model.Torneo;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.InscripcionRepository;
import com.sistemasdistr.basico.repository.TorneoRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoRepository torneoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UserRepository userRepository;

    public TorneoController(TorneoRepository torneoRepository, InscripcionRepository inscripcionRepository, UserRepository userRepository) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.userRepository = userRepository;
    }

    // Listar todos los torneos
    @GetMapping
    public String listarTorneos(Model model) {
        model.addAttribute("torneos", torneoRepository.findAll());
        return "torneos/lista";
    }

    // Mostrar formulario de creación
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "torneos/formulario";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        torneoRepository.findById(id).ifPresent(t -> model.addAttribute("torneo", t));
        return "torneos/formulario";
    }

    // Guardar o actualizar
    @PostMapping("/guardar")
    public String guardarTorneo(@ModelAttribute Torneo torneo) {
        torneoRepository.save(torneo);
        return "redirect:/torneos";
    }

    // Eliminar torneo (borrando antes a los apuntados para que la BD no explote)
    @GetMapping("/eliminar/{id}")
    public String eliminarTorneo(@PathVariable Integer id) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByTorneoId(id);
        inscripcionRepository.deleteAll(inscripciones);
        torneoRepository.deleteById(id);
        return "redirect:/torneos";
    }

    // NUEVO: Método para que un usuario se apunte eligiendo su equipo
    @PostMapping("/inscribirse")
    public String inscribirse(@RequestParam Integer torneoId,
                            @RequestParam String p1, @RequestParam String p2,
                            @RequestParam String p3, @RequestParam String p4,
                            @RequestParam String p5, @RequestParam String p6) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User usuario = userRepository.findUserByUsername(auth.getName());
        Torneo torneo = torneoRepository.findById(torneoId).orElse(null);

        if(usuario != null && torneo != null) {
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setUsuario(usuario);
            inscripcion.setTorneo(torneo);
            
            // Aquí usamos los setters que SÍ existen en tu modelo
            inscripcion.setPokemon1(p1);
            inscripcion.setPokemon2(p2);
            inscripcion.setPokemon3(p3);
            inscripcion.setPokemon4(p4);
            inscripcion.setPokemon5(p5);
            inscripcion.setPokemon6(p6);
            
            inscripcionRepository.save(inscripcion);
        }
        return "redirect:/torneos";
    }
}