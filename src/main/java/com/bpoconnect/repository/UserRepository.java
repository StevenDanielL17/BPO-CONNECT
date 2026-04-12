package com.bpoconnect.repository;

import com.bpoconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    boolean existsByEmailIgnoreCase(String email);
}
