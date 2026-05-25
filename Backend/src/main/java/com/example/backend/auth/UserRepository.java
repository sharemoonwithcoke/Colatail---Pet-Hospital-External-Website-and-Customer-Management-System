package com.example.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<SystemUser, UUID> {
    Optional<SystemUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
