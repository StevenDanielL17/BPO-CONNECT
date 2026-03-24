package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("AGENT")
@SuppressWarnings("unused")
public class SupportAgent extends User {
    private String teamId;
    private String currentStatus;
    private double qaScore;
    private double avgHandlingTime;

    public SupportAgent() {} // No-arg constructor for JPA

    public SupportAgent(String userId, String username, String email, String password, String teamId) {
        super(userId, username, email, password, "Agent");
        this.teamId = teamId;
        this.currentStatus = "Idle";
    }

    public void handleInboundCall() {
        this.currentStatus = "Connected";
    }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
}


