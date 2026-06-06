package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
}