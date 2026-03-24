package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@SuppressWarnings("unused")
public class EmailTicket extends Ticket {
    private String emailThreadId;

    public EmailTicket() {} // No-arg constructor for JPA

    public EmailTicket(String ticketId, String customerId, String agentId, String severity, String description, String emailThreadId) {
        super(ticketId, customerId, agentId, severity, description, "Email");
        this.emailThreadId = emailThreadId;
    }

    public void threadEmailReply(String emailBody) {
        // logic to thread
    }

    @Override
    public String getDetails() {
        return "Email Ticket [" + ticketId + "] with thread ID: " + emailThreadId;
    }
}


