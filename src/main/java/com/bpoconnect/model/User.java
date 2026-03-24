package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class User {
    @Id
    protected String userId;
    protected String username;
    protected String email;
    protected String password;
    protected String role;
    protected String lastLoginTime;
    protected boolean isActive;

    public User() {} // No-arg constructor for JPA

    public User(String userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    public boolean login(String inputPassword) {
        if (this.password.equals(inputPassword)) {
            this.lastLoginTime = java.time.LocalDateTime.now().toString();
            return true;
        }
        return false;
    }

    public void logout() {
        // Implementation
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}


