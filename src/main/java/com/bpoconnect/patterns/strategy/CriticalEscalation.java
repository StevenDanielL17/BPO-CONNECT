package com.bpoconnect.patterns.strategy;

public class CriticalEscalation implements IEscalationStrategy {
    @Override
    public int getThresholdMinutes() {
        return 30; // 30 minutes
    }

    @Override
    public void escalate(String ticketId) {
        System.out.println("[CriticalEscalation] Ticket " + ticketId + " exceeded 30 mins! Routing and paging on-call manager.");
    }
}
