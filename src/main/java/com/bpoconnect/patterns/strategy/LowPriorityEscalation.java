package com.bpoconnect.patterns.strategy;

public class LowPriorityEscalation implements IEscalationStrategy {
    @Override
    public int getThresholdMinutes() {
        return 1440; // 24 hours
    }

    @Override
    public void escalate(String ticketId) {
        System.out.println("[LowPriorityEscalation] Ticket " + ticketId + " exceeded 24 hours. Standard routing applied.");
    }
}
