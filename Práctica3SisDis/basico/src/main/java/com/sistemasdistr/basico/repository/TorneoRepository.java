package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Gestor de la base de datos para los torneos.
// Nos proporciona automáticamente las operaciones para guardar, buscar, editar y borrar los eventos.
@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
}