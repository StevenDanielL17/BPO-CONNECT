package com.bpoconnect.patterns.factory;

import com.bpoconnect.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TicketFactory {

    public Ticket createTicket(String channel, String customerId, String agentId, String severity, String description, String referenceId) {
        if (channel == null || channel.trim().isEmpty()) {
            channel = "general";
        }

        String normalizedChannel = channel.trim().toLowerCase();
        String ticketId = UUID.randomUUID().toString().substring(0, 8);
        switch (normalizedChannel) {
            case "voice":
                return new VoiceTicket(ticketId, customerId, agentId, severity, description, referenceId, "Unknown");
            case "email":
                return new EmailTicket(ticketId, customerId, agentId, severity, description, referenceId);
            case "chat":
                return new ChatTicket(ticketId, customerId, agentId, severity, description, referenceId);
            default:
                // Handle all other channels (billing, technical, account, general, etc.)
                return new GeneralTicket(ticketId, customerId, agentId, severity, description, channel, referenceId);
        }
    }
}
