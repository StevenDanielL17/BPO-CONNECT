package com.bpoconnect.service;

import com.bpoconnect.model.SLA;
import com.bpoconnect.model.Ticket;
import com.bpoconnect.patterns.factory.TicketFactory;
import com.bpoconnect.patterns.observer.ITicketObserver;
import com.bpoconnect.patterns.strategy.CriticalEscalation;
import com.bpoconnect.patterns.strategy.HighPriorityEscalation;
import com.bpoconnect.patterns.strategy.LowPriorityEscalation;
import com.bpoconnect.repository.SLARepository;
import com.bpoconnect.repository.TicketRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketFactory ticketFactory;
    private final TicketRepository ticketRepository;
    private final SLARepository slaRepository;
    private final List<ITicketObserver> observers;


    public TicketService(TicketFactory ticketFactory, TicketRepository ticketRepository, SLARepository slaRepository, List<ITicketObserver> observers) {
        this.ticketFactory = ticketFactory;
        this.ticketRepository = ticketRepository;
        this.slaRepository = slaRepository;
        this.observers = observers;
    }

    public void registerObserver(ITicketObserver observer) {
        // Observers are auto-injected by Spring
    }

    private void notifyObservers(String ticketId, String newStatus) {
        for (ITicketObserver observer : observers) {
            observer.update(ticketId, newStatus);
        }
    }

    @Transactional
    public Ticket createTicket(String channel, String customerId, String agentId, String severity, String description, String referenceId) {
        String normalizedSeverity = (severity == null || severity.trim().isEmpty()) ? "Low" : severity.trim();
        Ticket ticket = ticketFactory.createTicket(channel, customerId, agentId, normalizedSeverity, description, referenceId);
        
        // Assign SLA Strategy
        SLA sla;
        String slaId = "SLA-" + UUID.randomUUID().toString().substring(0, 4);
        switch (normalizedSeverity.toLowerCase()) {
            case "critical":
                sla = new SLA(slaId, normalizedSeverity, new CriticalEscalation());
                break;
            case "high":
                sla = new SLA(slaId, normalizedSeverity, new HighPriorityEscalation());
                break;
            default:
                sla = new SLA(slaId, normalizedSeverity, new LowPriorityEscalation());
                break;
        }

        ticket = ticketRepository.save(ticket);
        slaRepository.save(sla);
        
        System.out.println("[TicketService] Created ticket " + ticket.getTicketId() + ". Assigned SLA threshold: " + sla.getEscalationThreshold() + " mins.");
        notifyObservers(ticket.getTicketId(), ticket.getStatus());
        
        return ticket;
    }

    public Ticket getTicket(String ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public void updateTicketStatus(String ticketId, String newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            ticket.updateStatus(newStatus);
            ticketRepository.save(ticket);
            notifyObservers(ticketId, newStatus);
        }
    }

    @Transactional
    public void triggerEscalation(String ticketId) {
        // In a real app, we'd find the SLA associated with the ticket's priority
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            String severity = normalizeSeverity(ticket.getSeverity());
            SLA sla = slaRepository.findFirstByPriorityLevel(severity).orElse(null);
            if (sla == null) {
                // Default if not found
                sla = new SLA("DEF", severity, new LowPriorityEscalation());
            } else {
                // Re-attach strategy since it's @Transient
                attachStrategy(sla);
            }
            sla.triggerEscalation(ticketId);
            updateTicketStatus(ticketId, "Escalated");
        }
    }

    private void attachStrategy(SLA sla) {
        String priorityLevel = normalizeSeverity(sla.getPriorityLevel());
        switch (priorityLevel.toLowerCase()) {
            case "critical": sla.setStrategy(new CriticalEscalation()); break;
            case "high": sla.setStrategy(new HighPriorityEscalation()); break;
            default: sla.setStrategy(new LowPriorityEscalation()); break;
        }
    }

    private String normalizeSeverity(String severity) {
        if (severity == null || severity.trim().isEmpty()) {
            return "Low";
        }
        return severity.trim();
    }
}
