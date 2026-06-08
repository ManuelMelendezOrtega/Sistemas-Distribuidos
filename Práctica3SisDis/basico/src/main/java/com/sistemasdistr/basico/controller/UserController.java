package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para la gestión y administración de usuarios.
 * Al estar bajo la ruta "/users", es un panel protegido reservado para administradores.
 * Permite visualizar el listado completo de cuentas, modificar sus roles y eliminar usuarios del sistema.
 */
@Controller
@RequestMapping("/users") 
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Constructor que inyecta las herramientas necesarias para interactuar con las tablas de usuarios y roles de la base de datos.
    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Carga la pantalla principal del panel de administración. 
    // Recupera todos los usuarios registrados en el sistema y los envía a la vista para mostrarlos en forma de lista o tabla.
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        return "usuarios/lista";
    }

    // Prepara la pantalla de edición de un perfil. Busca al usuario por su ID y, si existe, lo envía al formulario.
    // También extrae todos los roles disponibles en el sistema (ej. USER, ADMIN) para poder cargarlos en un desplegable y permitir cambiarlos.
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

    // Recibe los nuevos datos del formulario de edición y los aplica.
    // En lugar de machacar todo el registro, primero busca al usuario original para mantener intacta su contraseña y otros datos internos.
    // Solo actualiza el correo, el nombre mostrado y el rol antes de guardar definitivamente los cambios.
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute User usuario) {
        Optional<User> userOriginal = userRepository.findById(usuario.getId());
        if (userOriginal.isPresent()) {
            User u = userOriginal.get();
            u.setEmail(usuario.getEmail());
            u.setNombreUsuario(usuario.getNombreUsuario());
            u.setUserRole(usuario.getUserRole()); 
            userRepository.save(u);
        }
        return "redirect:/users";
    }

    // Elimina a un usuario de la base de datos a través de su ID.
    // Comprueba el nombre de la cuenta para impedir que el usuario "admin" 
    // pueda ser borrado accidentalmente, lo que dejaría al sistema sin ningún administrador.
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            if (!"admin".equals(user.getUsername())) {
                userRepository.delete(user);
            }
        });
        return "redirect:/users";
    }
}