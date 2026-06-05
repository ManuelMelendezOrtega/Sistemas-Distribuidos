package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.PokemonAtrapado;
import com.sistemasdistr.basico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PokemonAtrapadoRepository extends JpaRepository<PokemonAtrapado, Long> {
    @Query("SELECT p FROM PokemonAtrapado p WHERE p.entrenador.usuario = :usuario")
    List<PokemonAtrapado> findByEntrenadorUsuario(@Param("usuario") User usuario);
}