package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Gestor de la base de datos para los usuarios.
// Permite buscar rápidamente a una persona por su nombre de usuario.
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findUserByUsername(String username);
}