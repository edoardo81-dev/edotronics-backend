package com.example.repository;

import com.example.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsernameIgnoreCase(String username);
    
    boolean existsByUsernameIgnoreCase(String username);

}
