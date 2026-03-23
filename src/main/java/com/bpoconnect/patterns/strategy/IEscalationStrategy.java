package com.bpoconnect.patterns.strategy;

public interface IEscalationStrategy {
    int getThresholdMinutes();
    void escalate(String ticketId);
}
