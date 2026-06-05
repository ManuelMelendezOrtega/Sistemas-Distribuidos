package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Entrenador;
import com.sistemasdistr.basico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
    
    // Busca y devuelve todos los entrenadores que pertenecen a un usuario concreto
    List<Entrenador> findByUsuario(User usuario);
}