package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@SuppressWarnings("unused")
public class GeneralTicket extends Ticket {
    private String category;

    public GeneralTicket() {} // No-arg constructor for JPA

    public GeneralTicket(String ticketId, String customerId, String agentId, String severity, String description, String channel, String category) {
        super(ticketId, customerId, agentId, severity, description, channel);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String getDetails() {
        return "Ticket [" + ticketId + "] - " + category + " (" + channel + ")";
    }
}
