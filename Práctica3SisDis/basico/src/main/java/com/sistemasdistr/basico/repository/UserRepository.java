package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Interfaz repositorio para la base de datos.
// Permite ejecutar consultas automáticas en la tabla de usuarios sin escribir código SQL.
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findUserByUsername(String username);

    Optional<User> findByUsername(String username);
}