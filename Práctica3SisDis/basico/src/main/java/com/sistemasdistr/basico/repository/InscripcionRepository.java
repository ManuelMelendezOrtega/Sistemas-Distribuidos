package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    List<Inscripcion> findByTorneoId(Integer torneoId);
    List<Inscripcion> findByUsuarioId(Integer usuarioId);
}