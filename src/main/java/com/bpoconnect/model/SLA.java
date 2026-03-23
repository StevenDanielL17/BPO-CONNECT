package com.bpoconnect.model;

import com.bpoconnect.patterns.strategy.IEscalationStrategy;
import jakarta.persistence.*;

@Entity
@Table(name = "slas")
public class SLA {
    @Id
    private String slaId;
    private String priorityLevel;
    
    @Transient
    private IEscalationStrategy escalationStrategy;

    public SLA() {} // No-arg constructor for JPA

    public SLA(String slaId, String priorityLevel, IEscalationStrategy strategy) {
        this.slaId = slaId;
        this.priorityLevel = priorityLevel;
        this.escalationStrategy = strategy;
    }

    public void setStrategy(IEscalationStrategy strategy) {
        this.escalationStrategy = strategy;
    }

    public void triggerEscalation(String ticketId) {
        if (escalationStrategy != null) {
            escalationStrategy.escalate(ticketId);
        }
    }

    public int getEscalationThreshold() {
        return escalationStrategy.getThresholdMinutes();
    }
    
    public String getPriorityLevel() { return priorityLevel; }
}
