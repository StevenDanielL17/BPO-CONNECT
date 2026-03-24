package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("LEADER")
@SuppressWarnings("unused")
public class TeamLeader extends User {
    private String teamId;

    public TeamLeader() {}

    public TeamLeader(String userId, String username, String email, String password, String teamId) {
        super(userId, username, email, password, "Team Leader");
        this.teamId = teamId;
    }
}


