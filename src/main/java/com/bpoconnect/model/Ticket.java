package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Ticket {
    @Id
    protected String ticketId;
    protected String customerId;
    protected String agentId;
    protected String issueType;
    protected String subType;
    protected String severity;
    protected String description;
    protected String channel;
    protected String status; // New, Open, InProgress, Resolved, Closed, Escalated

    public Ticket() {} // No-arg constructor for JPA

    public Ticket(String ticketId, String customerId, String agentId, String severity, String description, String channel) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.agentId = agentId;
        this.severity = severity;
        this.description = description;
        this.channel = channel;
        this.status = "New";
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public String getTicketId() { return ticketId; }
    public String getStatus() { return status; }
    public String getSeverity() { return severity; }
    public String getChannel() { return channel; }
    public String getCustomerId() { return customerId; }
    public String getAgentId() { return agentId; }
    public String getDescription() { return description; }
    public String getIssueType() { return issueType; }
    public String getSubType() { return subType; }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    // Abstract method to be implemented by Concrete Products
    public abstract String getDetails();
}


