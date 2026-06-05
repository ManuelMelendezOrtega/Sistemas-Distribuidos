package com.sistemasdistr.basico.config;

import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

// Servicio personalizado de seguridad. 
// Enlaza nuestro sistema de usuarios en base de datos con el sistema interno de Spring Security.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Busca un usuario en la base de datos por su nombre de usuario y le asigna sus permisos correspondientes.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException   {
        User user = userRepository.findUserByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getUserRole().getRoleName()))
        );
    }
}