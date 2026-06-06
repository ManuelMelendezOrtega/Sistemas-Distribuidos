package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users") // Protegido por tu SecurityConfig (/users/**)
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Listar todos los usuarios (READ)
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        return "usuarios/lista";
    }

    // Mostrar formulario de edición (UPDATE - Vista)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("usuario", userOpt.get());
            model.addAttribute("roles", roleRepository.findAll());
            return "usuarios/editar";
        }
        return "redirect:/users";
    }

    // Guardar cambios del usuario (UPDATE - Acción)
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute User usuario) {
        // Buscamos el usuario original para no perder datos como la contraseña o fecha de acceso
        Optional<User> userOriginal = userRepository.findById(usuario.getId());
        if (userOriginal.isPresent()) {
            User u = userOriginal.get();
            u.setEmail(usuario.getEmail());
            u.setNombreUsuario(usuario.getNombreUsuario());
            u.setUserRole(usuario.getUserRole()); // Cambiar el Rol
            userRepository.save(u);
        }
        return "redirect:/users";
    }

    // Eliminar un usuario (DELETE)
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        // Evitamos que el admin se borre a sí mismo de forma accidental
        userRepository.findById(id).ifPresent(user -> {
            if (!"admin".equals(user.getUsername())) {
                userRepository.delete(user);
            }
        });
        return "redirect:/users";
    }
}