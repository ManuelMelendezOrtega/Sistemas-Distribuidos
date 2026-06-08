package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.*;
import com.sistemasdistr.basico.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de gestión de torneos.
 * Se encarga de mostrar la lista de torneos, permitir la creación, edición y eliminación de los mismos, 
 * así como gestionar la lógica para que los usuarios se inscriban con sus equipos, se den de baja o vean a los participantes.
 */
@Controller
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoRepository torneoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UserRepository userRepository;
    private final EquipoRepository equipoRepository; 

    // Constructor que inyecta las dependencias necesarias para acceder a las bases de datos de torneos, inscripciones, usuarios y equipos.
    public TorneoController(TorneoRepository torneoRepository, InscripcionRepository inscripcionRepository, UserRepository userRepository, EquipoRepository equipoRepository) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.userRepository = userRepository;
        this.equipoRepository = equipoRepository;
    }

    // Gestiona la vista principal de torneos. Recupera todos los torneos disponibles.
    // Además, identifica al usuario autenticado para extraer los IDs de los torneos en los que ya está inscrito 
    // y carga sus equipos creados para enviarlos a la vista, adaptando así la interfaz a su estado actual.
    @GetMapping
    public String listarTorneos(Model model) {
        model.addAttribute("torneos", torneoRepository.findAll());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User usuario = userRepository.findUserByUsername(auth.getName());
        
        if (usuario != null) {
            List<Integer> misTorneosIds = inscripcionRepository.findByUsuarioId(usuario.getId())
                    .stream().map(ins -> ins.getTorneo().getId()).collect(Collectors.toList());
            model.addAttribute("misTorneosIds", misTorneosIds);
            
            List<Equipo> misEquipos = equipoRepository.findByUsuario(usuario);
            model.addAttribute("misEquipos", misEquipos != null ? misEquipos : new ArrayList<>());
        } else {
            model.addAttribute("misEquipos", new ArrayList<>());
        }

        return "torneos/lista";
    }

    // Prepara y muestra un formulario en blanco creando una nueva instancia de Torneo para que el usuario introduzca los datos.
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "torneos/formulario";
    }

    // Busca un torneo existente por su ID en la base de datos y, si lo encuentra, lo envía al formulario para modificar sus datos.
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        torneoRepository.findById(id).ifPresent(t -> model.addAttribute("torneo", t));
        return "torneos/formulario";
    }

    // Recibe los datos del formulario de creación o edición, guarda el torneo en la base de datos y redirige a la lista principal.
    @PostMapping("/guardar")
    public String guardarTorneo(@ModelAttribute Torneo torneo) {
        torneoRepository.save(torneo);
        return "redirect:/torneos";
    }

    // Elimina un torneo específico de la base de datos. Para mantener la integridad de los datos, 
    // primero busca y elimina todas las inscripciones asociadas a ese torneo antes de borrar el torneo en sí.
    @GetMapping("/eliminar/{id}")
    public String eliminarTorneo(@PathVariable Integer id) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByTorneoId(id);
        inscripcionRepository.deleteAll(inscripciones);
        torneoRepository.deleteById(id);
        return "redirect:/torneos";
    }

    // Procesa la inscripción de un usuario a un torneo concreto. Identifica al usuario, valida el torneo y el equipo seleccionado,
    // y crea un registro de inscripción copiando el estado actual de los 6 Pokémon del equipo para guardarlos de forma fija.
    @PostMapping("/inscribirse")
    public String inscribirse(@RequestParam Integer torneoId, @RequestParam Integer equipoId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User usuario = userRepository.findUserByUsername(auth.getName());
        Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
        Equipo equipo = equipoRepository.findById(equipoId).orElse(null);

        if(usuario != null && torneo != null && equipo != null) {
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setUsuario(usuario);
            inscripcion.setTorneo(torneo);
            inscripcion.setPokemon1(equipo.getP1());
            inscripcion.setPokemon2(equipo.getP2());
            inscripcion.setPokemon3(equipo.getP3());
            inscripcion.setPokemon4(equipo.getP4());
            inscripcion.setPokemon5(equipo.getP5());
            inscripcion.setPokemon6(equipo.getP6());
            inscripcionRepository.save(inscripcion);
            redirectAttributes.addFlashAttribute("exitoInscripcion", "¡Te has inscrito correctamente usando tu equipo: " + equipo.getNombreEquipo() + "!");
        }
        return "redirect:/torneos";
    }

    // Cancela la participación de un usuario en un torneo. Identifica al usuario activo, busca todas las inscripciones del torneo
    // y elimina específicamente aquella que coincide con el ID del usuario logueado.
    @GetMapping("/desapuntar/{torneoId}")
    public String desapuntarDelTorneo(@PathVariable Integer torneoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User usuario = userRepository.findUserByUsername(auth.getName());
        if (usuario != null) {
            List<Inscripcion> inscripciones = inscripcionRepository.findByTorneoId(torneoId);
            for (Inscripcion ins : inscripciones) {
                if (ins.getUsuario().getId().equals(usuario.getId())) {
                    inscripcionRepository.delete(ins);
                    break;
                }
            }
        }
        return "redirect:/torneos";
    }

    // Busca un torneo por su ID y recupera todas sus inscripciones para mostrar en una vista detallada 
    // la lista completa de usuarios que participan en él. Si el torneo no existe, redirige al listado principal.
    @GetMapping("/{id}/participantes")
    public String verParticipantes(@PathVariable Integer id, Model model) {
        Torneo torneo = torneoRepository.findById(id).orElse(null);
        if (torneo != null) {
            model.addAttribute("torneo", torneo);
            model.addAttribute("inscripciones", inscripcionRepository.findByTorneoId(id));
            return "torneos/participantes";
        }
        return "redirect:/torneos";
    }
}