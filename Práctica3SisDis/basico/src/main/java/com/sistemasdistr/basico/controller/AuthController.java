package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.*;
import com.sistemasdistr.basico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new User());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute User usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Role rolUsuario = roleRepository.findByRoleName("USER");
        if (rolUsuario != null) usuario.setUserRole(rolUsuario);
        userRepository.save(usuario);
        return "redirect:/login";
    }
}