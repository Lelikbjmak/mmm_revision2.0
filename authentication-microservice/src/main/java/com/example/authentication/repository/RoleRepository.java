package com.example.authentication.repository;

import com.example.authentication.model.EnumRole;
import com.example.authentication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(EnumRole role);
}
