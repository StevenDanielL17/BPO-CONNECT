package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("QA")
public class QualityAnalyst extends User {
    
    public QualityAnalyst() {}

    public QualityAnalyst(String userId, String username, String email, String password) {
        super(userId, username, email, password, "Quality Analyst");
    }
}
