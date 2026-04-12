package com.bpoconnect.service;

import com.bpoconnect.model.Ticket;
import com.bpoconnect.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportService {

    private final TicketRepository ticketRepository;
    private final AuditService auditService;

    public ReportService(TicketRepository ticketRepository, AuditService auditService) {
        this.ticketRepository = ticketRepository;
        this.auditService = auditService;
    }

    public String generateDailyReport(String userId) {
        Map<String, Object> summary = buildSummary();
        auditService.log(userId, null, "REPORT_VIEWED", "Daily summary viewed");
        return String.format(Locale.ROOT,
                "Tickets: %s | Resolved: %s | Escalated: %s | Open: %s",
                summary.get("totalTickets"),
                summary.get("resolvedTickets"),
                summary.get("escalatedTickets"),
                summary.get("openTickets"));
    }

    public Map<String, Object> generateDailyReport() {
        return buildSummary();
    }

    public Map<String, Object> generateAgentPerformanceReport() {
        return buildSummary();
    }

    public Map<String, Object> generateQAReport() {
        return buildSummary();
    }

    public String generateDailyReportCsv(String userId) {
        List<Ticket> tickets = ticketRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ticketId,customerId,agentId,channel,severity,status,description\n");
        for (Ticket ticket : tickets) {
            csv.append(csvValue(ticket.getTicketId())).append(',')
                    .append(csvValue(ticket.getCustomerId())).append(',')
                    .append(csvValue(ticket.getAgentId())).append(',')
                    .append(csvValue(ticket.getChannel())).append(',')
                    .append(csvValue(ticket.getSeverity())).append(',')
                    .append(csvValue(ticket.getStatus())).append(',')
                    .append(csvValue(ticket.getDescription())).append('\n');
        }
        auditService.log(userId, null, "REPORT_EXPORTED", "CSV export generated with " + tickets.size() + " tickets");
        return csv.toString();
    }

    public void exportToCSV() {
        generateDailyReportCsv("SYSTEM");
    }

    private Map<String, Object> buildSummary() {
        List<Ticket> tickets = ticketRepository.findAll();
        long total = tickets.size();
        long resolved = tickets.stream().filter(ticket -> isStatus(ticket, "Resolved", "Closed")).count();
        long escalated = tickets.stream().filter(ticket -> "Escalated".equalsIgnoreCase(ticket.getStatus())).count();
        long open = tickets.stream().filter(ticket -> isStatus(ticket, "New", "Open", "InProgress", "PendingCustomer")).count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("generatedAt", LocalDateTime.now().toString());
        summary.put("totalTickets", total);
        summary.put("resolvedTickets", resolved);
        summary.put("escalatedTickets", escalated);
        summary.put("openTickets", open);
        summary.put("resolutionRate", total > 0 ? Math.round((resolved * 100.0) / total) : 0);
        summary.put("tickets", tickets);
        return summary;
    }

    private boolean isStatus(Ticket ticket, String... statuses) {
        if (ticket == null || ticket.getStatus() == null) {
            return false;
        }

        for (String status : statuses) {
            if (ticket.getStatus().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private String csvValue(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return '"' + safe + '"';
    }
}
