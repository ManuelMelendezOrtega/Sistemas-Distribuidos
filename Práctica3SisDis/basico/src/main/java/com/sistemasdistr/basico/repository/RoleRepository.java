package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Gestor de la base de datos para los permisos.
// Proporciona automáticamente las funciones para buscar, crear o borrar roles en el sistema.
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}