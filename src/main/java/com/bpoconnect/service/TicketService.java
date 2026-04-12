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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketFactory ticketFactory;
    private final TicketRepository ticketRepository;
    private final SLARepository slaRepository;
    private final List<ITicketObserver> observers;
    private final AuditService auditService;


    public TicketService(TicketFactory ticketFactory, TicketRepository ticketRepository, SLARepository slaRepository, List<ITicketObserver> observers, AuditService auditService) {
        this.ticketFactory = ticketFactory;
        this.ticketRepository = ticketRepository;
        this.slaRepository = slaRepository;
        this.observers = observers;
        this.auditService = auditService;
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
        
        SLA sla = slaRepository.findFirstByPriorityLevelIgnoreCase(normalizedSeverity).orElse(null);

        if (sla == null) {
            String slaId = "SLA-" + UUID.randomUUID().toString().substring(0, 4);
            sla = switch (normalizedSeverity.toLowerCase()) {
                case "critical" -> new SLA(slaId, normalizedSeverity, new CriticalEscalation());
                case "high" -> new SLA(slaId, normalizedSeverity, new HighPriorityEscalation());
                default -> new SLA(slaId, normalizedSeverity, new LowPriorityEscalation());
            };
            slaRepository.save(sla);
        } else {
            attachStrategy(sla);
        }

        ticket = ticketRepository.save(Objects.requireNonNull(ticket, "ticket"));
        
        System.out.println("[TicketService] Created ticket " + ticket.getTicketId() + ". Assigned SLA threshold: " + sla.getEscalationThreshold() + " mins.");
        notifyObservers(ticket.getTicketId(), ticket.getStatus());
        auditService.log(ticket.getAgentId(), ticket.getTicketId(), "TICKET_CREATED", "Channel=" + ticket.getChannel() + ", Severity=" + ticket.getSeverity());
        
        return ticket;
    }

    public Ticket getTicket(String ticketId) {
        return ticketRepository.findById(Objects.requireNonNull(ticketId, "ticketId")).orElse(null);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public void updateTicketStatus(String ticketId, String newStatus) {
        Ticket ticket = ticketRepository.findById(Objects.requireNonNull(ticketId, "ticketId")).orElse(null);
        if (ticket != null) {
            ticket.updateStatus(newStatus);
            ticketRepository.save(ticket);
            notifyObservers(ticketId, newStatus);
            auditService.log(ticket.getAgentId(), ticketId, "TICKET_STATUS_CHANGED", newStatus);
        }
    }

    @Transactional
    public void triggerEscalation(String ticketId) {
        // In a real app, we'd find the SLA associated with the ticket's priority
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            String severity = normalizeSeverity(ticket.getSeverity());
            SLA sla = slaRepository.findFirstByPriorityLevelIgnoreCase(severity).orElse(null);
            if (sla == null) {
                // Default if not found
                sla = new SLA("DEF", severity, new LowPriorityEscalation());
            } else {
                // Re-attach strategy since it's @Transient
                attachStrategy(sla);
            }
            sla.triggerEscalation(ticketId);
            updateTicketStatus(ticketId, "Escalated");
            auditService.log(ticket.getAgentId(), ticketId, "TICKET_ESCALATED", "Manual or SLA-triggered escalation");
        }
    }

    private void attachStrategy(SLA sla) {
        String priorityLevel = normalizeSeverity(sla.getPriorityLevel());
        switch (priorityLevel.toLowerCase()) {
            case "critical" -> sla.setStrategy(new CriticalEscalation());
            case "high" -> sla.setStrategy(new HighPriorityEscalation());
            default -> sla.setStrategy(new LowPriorityEscalation());
        }
    }

    @Transactional
    public Ticket deleteTicket(String ticketId, String requestedBy) {
        Ticket ticket = ticketRepository.findById(Objects.requireNonNull(ticketId, "ticketId"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        auditService.log(requestedBy, ticketId, "TICKET_DELETED", "Permanent delete requested");
        ticketRepository.delete(Objects.requireNonNull(ticket, "ticket"));
        return ticket;
    }

    @Transactional
    public Ticket transferTicket(String ticketId, String targetAgentId, String requestedBy) {
        Ticket ticket = ticketRepository.findById(Objects.requireNonNull(ticketId, "ticketId"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        String safeTargetAgentId = Objects.requireNonNull(targetAgentId, "targetAgentId").trim();
        if (safeTargetAgentId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target agent is required");
        }

        ticket.setAgentId(safeTargetAgentId);
        ticket.updateStatus(ticket.getStatus() == null ? "Open" : ticket.getStatus());
        Ticket savedTicket = ticketRepository.save(ticket);
        auditService.log(requestedBy, ticketId, "TICKET_TRANSFERRED", "Transferred to " + safeTargetAgentId);
        notifyObservers(ticketId, savedTicket.getStatus());
        return savedTicket;
    }

    private String normalizeSeverity(String severity) {
        if (severity == null || severity.trim().isEmpty()) {
            return "Low";
        }
        return severity.trim();
    }
}
