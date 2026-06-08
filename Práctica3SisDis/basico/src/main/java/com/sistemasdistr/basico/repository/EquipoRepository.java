package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Equipo;
import com.sistemasdistr.basico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Interfaz para gestionar las consultas de los equipos en la base de datos.
// Incluye un método para recuperar de golpe todos los equipos creados por un usuario concreto.
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    
    List<Equipo> findByUsuario(User usuario);
}