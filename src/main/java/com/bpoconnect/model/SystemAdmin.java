package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class SystemAdmin extends User {

    public SystemAdmin() {}

    public SystemAdmin(String userId, String username, String email, String password) {
        super(userId, username, email, password, "Admin");
    }
}
