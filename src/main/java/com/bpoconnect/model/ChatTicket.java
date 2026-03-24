package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@SuppressWarnings("unused")
public class ChatTicket extends Ticket {
    private String chatSessionId;

    public ChatTicket() {} // No-arg constructor for JPA

    public ChatTicket(String ticketId, String customerId, String agentId, String severity, String description, String chatSessionId) {
        super(ticketId, customerId, agentId, severity, description, "Chat");
        this.chatSessionId = chatSessionId;
    }

    public void attachTranscript(String transcript) {
        // logic
    }

    @Override
    public String getDetails() {
        return "Chat Ticket [" + ticketId + "] with session ID: " + chatSessionId;
    }
}


