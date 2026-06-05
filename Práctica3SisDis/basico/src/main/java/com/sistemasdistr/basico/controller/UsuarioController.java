package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;

import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORTANTE
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // HERRAMIENTA PARA ENCRIPTAR

    // 1. Mostrar lista de usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        return "lista_usuarios";
    }

    // 2. Formulario para nuevo usuario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "formulario_usuarios";
    }

    // 3. Guardar usuario
    @PostMapping
    public String guardarUsuario(@ModelAttribute User usuario) {
        // Cogemos la contraseña que escribió el usuario y la encriptamos
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        
        userRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        User usuarioABorrar = userRepository.findById(id).orElse(null);
        
        if (usuarioABorrar != null && usuarioABorrar.getUsername().equals(principal.getName())) {
            // Mandamos el mensaje de error
            redirectAttributes.addFlashAttribute("error", "⛔ No puedes eliminar tu propia cuenta mientras estás conectado.");
            return "redirect:/usuarios"; 
        }
        
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("exito", "✅ Usuario eliminado correctamente.");
        return "redirect:/usuarios";
    }
}