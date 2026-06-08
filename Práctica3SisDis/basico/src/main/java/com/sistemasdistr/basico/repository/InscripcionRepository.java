package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Gestor de la base de datos para las inscripciones.
// Nos permite buscar rápidamente todos los jugadores apuntados a un torneo o todo el historial de un usuario.
@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    
    List<Inscripcion> findByTorneoId(Integer torneoId);
    
    List<Inscripcion> findByUsuarioId(Integer usuarioId);
}