package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@SuppressWarnings("unused")
public class VoiceTicket extends Ticket {
    private String callRecordId;
    private String ani;

    public VoiceTicket() {} // No-arg constructor for JPA

    public VoiceTicket(String ticketId, String customerId, String agentId, String severity, String description, String callRecordId, String ani) {
        super(ticketId, customerId, agentId, severity, description, "Voice");
        this.callRecordId = callRecordId;
        this.ani = ani;
    }

    public void linkCallRecord(String callRecordId) {
        this.callRecordId = callRecordId;
    }

    @Override
    public String getDetails() {
        return "Voice Ticket [" + ticketId + "] from ANI: " + ani;
    }
}


