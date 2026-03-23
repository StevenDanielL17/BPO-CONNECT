package com.bpoconnect.controller;

import com.bpoconnect.model.*;
import com.bpoconnect.patterns.singleton.ScreenPopController;
import com.bpoconnect.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BpoApiController {

    private final TicketService ticketService;
    private final ScreenPopController screenPopController;
    private final KnowledgeBaseService kbService;
    private final ReportService reportService;
    private final QualityService qualityService;

    @Autowired
    public BpoApiController(TicketService ticketService, 
                            ScreenPopController screenPopController,
                            KnowledgeBaseService kbService,
                            ReportService reportService,
                            QualityService qualityService) {
        this.ticketService = ticketService;
        this.screenPopController = screenPopController;
        this.kbService = kbService;
        this.reportService = reportService;
        this.qualityService = qualityService;
    }

    @GetMapping("/evaluations/{agentId}")
    public List<QualityEvaluation> getAgentEvaluations(@PathVariable String agentId) {
        return qualityService.getAgentEvaluations(agentId);
    }

    @PostMapping("/call")
    public Customer simulateInboundCall(@RequestBody Map<String, String> payload) {
        String ani = payload.get("ani");
        return screenPopController.handleInboundCall(ani);
    }

    @PostMapping("/tickets")
    public Ticket createTicket(@RequestBody Map<String, String> payload) {
        return ticketService.createTicket(
                payload.get("channel"),
                payload.get("customerId"),
                payload.get("agentId"),
                payload.get("severity"),
                payload.get("description"),
                payload.get("referenceId")
        );
    }

    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @PutMapping("/tickets/{ticketId}/status")
    public void updateTicketStatus(@PathVariable String ticketId, @RequestBody Map<String, String> payload) {
        ticketService.updateTicketStatus(ticketId, payload.get("status"));
    }

    @PostMapping("/tickets/{ticketId}/escalate")
    public void escalateTicket(@PathVariable String ticketId) {
        ticketService.triggerEscalation(ticketId);
    }

    @GetMapping("/kb/search")
    public List<KnowledgeBaseArticle> searchKnowledgeBase(@RequestParam String query) {
        return kbService.searchArticles(query);
    }

    @PostMapping("/reports/daily")
    public String generateDailyReport(@RequestParam String userId) {
        reportService.generateDailyReport(userId);
        return "Daily report generation triggered. Check console for details.";
    }
}
