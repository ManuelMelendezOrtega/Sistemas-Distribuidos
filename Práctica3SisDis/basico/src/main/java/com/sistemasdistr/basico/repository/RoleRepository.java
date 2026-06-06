package com.sistemasdistr.basico.repository;

import com.sistemasdistr.basico.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // JpaRepository ya nos da automáticamente métodos como findAll(), findById(), etc.
}