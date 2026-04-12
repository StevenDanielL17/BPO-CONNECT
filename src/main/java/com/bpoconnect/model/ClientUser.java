package com.bpoconnect.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT")
@SuppressWarnings("unused")
public class ClientUser extends User {

    public ClientUser() {
    }

    public ClientUser(String userId, String username, String email, String password) {
        super(userId, username, email, password, "Client");
    }
}