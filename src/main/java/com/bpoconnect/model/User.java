package com.bpoconnect.model;

import com.bpoconnect.util.PasswordUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class User {
    @Id
    protected String userId;
    protected String username;
    @Column(unique = true)
    protected String email;
    @JsonIgnore
    protected String password;
    protected String role;
    protected String lastLoginTime;
    protected boolean isActive;
    @JsonIgnore
    protected String passwordResetToken;
    @JsonIgnore
    protected LocalDateTime passwordResetTokenExpiry;

    public User() {} // No-arg constructor for JPA

    public User(String userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = PasswordUtil.hashPassword(password);
        this.role = role;
        this.isActive = true;
    }

    public boolean login(String inputPassword) {
        if (PasswordUtil.verifyPassword(inputPassword, this.password)) {
            this.lastLoginTime = LocalDateTime.now().toString();
            return true;
        }
        return false;
    }

    public void setPassword(String password) {
        this.password = PasswordUtil.hashPassword(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getEmail() { return email; }
    public String getLastLoginTime() { return lastLoginTime; }
    public boolean isActive() { return isActive; }
    public String getPasswordResetToken() { return passwordResetToken; }
    public LocalDateTime getPasswordResetTokenExpiry() { return passwordResetTokenExpiry; }

    public void startPasswordReset(String token, LocalDateTime expiry) {
        this.passwordResetToken = token;
        this.passwordResetTokenExpiry = expiry;
    }

    public void clearPasswordReset() {
        this.passwordResetToken = null;
        this.passwordResetTokenExpiry = null;
    }

    public void logout() {
        // Implementation
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}


