package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.dto.PokemonDTO;
import com.sistemasdistr.basico.model.Role;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

// Controlador principal (El "Recepcionista").
// Gestiona las páginas públicas, el sistema de registro seguro de usuarios y el buscador de la Pokédex.
@Controller
public class maincontroller {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor: Inyecta las bases de datos y la herramienta para cifrar contraseñas
    public maincontroller(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Muestra la página de inicio
    @GetMapping("/")
    public String paginaPrincipal() {
        return "index"; 
    }

    // Entrega un formulario en blanco para que un nuevo usuario se registre
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new User());
        }
        return "registro";
    }

    // Procesa el formulario de registro: verifica duplicados, encripta la clave y guarda en base de datos
    @PostMapping("/registro")
    public String registrarUsuario(User usuario, Model model) {
        
        User usuarioExistente = userRepository.findUserByUsername(usuario.getUsername());
        
        if (usuarioExistente != null) {
            model.addAttribute("errorUsuario", "El nombre de usuario '" + usuario.getUsername() + "' ya está registrado. Por favor, elige otro.");
            model.addAttribute("usuario", usuario); 
            return "registro"; 
        }

        String passEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passEncriptada);

        Role rolUsuarioNormal = roleRepository.findById(2).orElse(null);
        usuario.setUserRole(rolUsuarioNormal);

        userRepository.save(usuario);
        return "redirect:/login?exito";
    }

    // Muestra la pantalla del buscador de Pokémon
    @GetMapping("/buscador")
    public String mostrarBuscador() {
        return "buscador"; 
    }

    // Muestra la pantalla de inicio de sesión
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }

    // Se comunica con Python para buscar un Pokémon y "traduce" los errores JSON si algo falla
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