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
        return switch (normalizedChannel) {
            case "voice" -> new VoiceTicket(ticketId, customerId, agentId, severity, description, referenceId, "Unknown");
            case "email" -> new EmailTicket(ticketId, customerId, agentId, severity, description, referenceId);
            case "chat" -> new ChatTicket(ticketId, customerId, agentId, severity, description, referenceId);
            default -> new GeneralTicket(ticketId, customerId, agentId, severity, description, channel, referenceId);
        };
    }
}
