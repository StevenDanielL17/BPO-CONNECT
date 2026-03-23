package com.bpoconnect.patterns.strategy;

public class HighPriorityEscalation implements IEscalationStrategy {
    @Override
    public int getThresholdMinutes() {
        return 120; // 2 hours
    }

    @Override
    public void escalate(String ticketId) {
        System.out.println("[HighPriorityEscalation] Ticket " + ticketId + " exceeded 2 hours. Routing to Team Leader queue.");
    }
}
