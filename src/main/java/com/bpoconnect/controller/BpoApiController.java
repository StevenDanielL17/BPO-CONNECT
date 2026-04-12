package com.bpoconnect.controller;

import com.bpoconnect.model.*;
import com.bpoconnect.patterns.singleton.ScreenPopController;
import com.bpoconnect.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BpoApiController {

    private final TicketService ticketService;
    private final com.bpoconnect.patterns.singleton.ScreenPopController screenPopController;
    private final KnowledgeBaseService kbService;
    private final ReportService reportService;
    private final QualityService qualityService;
    private final QueueService queueService;
    private final AuditService auditService;
    private final InternalChatService internalChatService;
    private final ReminderService reminderService;
    private final UserService userService;

    private static final String SESSION_USER_ID = "AUTH_USER_ID";


    public BpoApiController(TicketService ticketService, 
                            ScreenPopController screenPopController,
                            KnowledgeBaseService kbService,
                            ReportService reportService,
                            QualityService qualityService,
                            QueueService queueService,
                            AuditService auditService,
                            InternalChatService internalChatService,
                            ReminderService reminderService,
                            UserService userService) {
        this.ticketService = ticketService;
        this.screenPopController = screenPopController;
        this.kbService = kbService;
        this.reportService = reportService;
        this.qualityService = qualityService;
        this.queueService = queueService;
        this.auditService = auditService;
        this.internalChatService = internalChatService;
        this.reminderService = reminderService;
        this.userService = userService;
    }

    @GetMapping("/evaluations/{agentId}")
    public List<QualityEvaluation> getAgentEvaluations(@PathVariable String agentId) {
        return qualityService.getAgentEvaluations(agentId);
    }

    @PostMapping("/evaluations")
    public QualityEvaluation submitEvaluation(@RequestBody Map<String, String> payload) {
        double score = Double.parseDouble(payload.getOrDefault("score", "0"));
        return qualityService.submitEvaluation(
                payload.get("ticketId"),
                payload.get("agentId"),
                payload.getOrDefault("evaluatorId", "QA"),
                score,
                payload.getOrDefault("feedback", "")
        );
    }

    @PostMapping("/evaluations/{evaluationId}/flag")
    public QualityEvaluation flagEvaluation(@PathVariable String evaluationId, @RequestBody Map<String, String> payload) {
        return qualityService.flagForCoaching(evaluationId, payload.getOrDefault("coachingNotes", "Flagged for coaching"));
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

    @PostMapping("/tickets/{ticketId}/transfer")
    public ResponseEntity<?> transferTicket(@PathVariable String ticketId, @RequestBody Map<String, String> payload, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        if (!isAgentOrLeaderOrAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Agent, supervisor, or admin access required"));
        }

        Ticket transferredTicket = ticketService.transferTicket(ticketId, payload.get("targetAgentId"), currentUser.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Ticket transferred",
                "ticketId", transferredTicket.getTicketId(),
                "agentId", transferredTicket.getAgentId(),
                "status", transferredTicket.getStatus()));
    }

    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable String ticketId, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required"));
        }

        Ticket deletedTicket = ticketService.deleteTicket(ticketId, currentUser.getUserId());
        return ResponseEntity.ok(Map.of("message", "Ticket deleted", "ticketId", deletedTicket.getTicketId()));
    }

    @GetMapping("/queue/stats")
    public Map<String, Object> getQueueStats() {
        return queueService.getRealTimeStats();
    }

    @GetMapping("/queue/next")
    public Ticket getNextQueueTicket() {
        return queueService.getNextTicket();
    }

    @GetMapping("/audit/logs")
    public List<AuditLog> getAuditLogs(@RequestParam(name = "limit", defaultValue = "25") int limit) {
        return auditService.getRecentLogs(limit);
    }

    @GetMapping("/chat/messages")
    public List<InternalChatMessage> getChatMessages(HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return internalChatService.getMessages(currentUser.getUserId());
    }

    @PostMapping("/chat/messages")
    public InternalChatMessage sendChatMessage(@RequestBody Map<String, String> payload, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return internalChatService.sendMessage(
                currentUser.getUserId(),
                payload.get("recipientId"),
                payload.get("message")
        );
    }

    @PatchMapping("/chat/messages/{messageId}/read")
    public InternalChatMessage markChatMessageRead(@PathVariable String messageId, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return internalChatService.markAsRead(messageId, currentUser.getUserId());
    }

    @GetMapping("/reminders")
    public List<FollowUpReminder> getReminders(HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return reminderService.getRemindersForAgent(currentUser.getUserId());
    }

    @PostMapping("/reminders")
    public FollowUpReminder createReminder(@RequestBody Map<String, String> payload, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return reminderService.createReminder(
                payload.get("ticketId"),
                currentUser.getUserId(),
                payload.get("reminderAt"),
                payload.get("notes")
        );
    }

    @PatchMapping("/reminders/{reminderId}/complete")
    public FollowUpReminder completeReminder(@PathVariable String reminderId, HttpSession session) {
        User currentUser = requireCurrentUser(session);
        return reminderService.completeReminder(reminderId, currentUser.getUserId());
    }

    @GetMapping("/kb/search")
    public List<KnowledgeBaseArticle> searchKnowledgeBase(@RequestParam String query) {
        return kbService.searchArticles(query);
    }

    @GetMapping("/kb/articles")
    public List<KnowledgeBaseArticle> getKnowledgeBaseArticles() {
        return kbService.getAllArticles();
    }

    @PostMapping("/kb/articles")
    public KnowledgeBaseArticle createKnowledgeBaseArticle(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String content = payload.get("content");
        String category = payload.getOrDefault("category", "General");

        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and content are required");
        }

        KnowledgeBaseArticle article = new KnowledgeBaseArticle();
        article.setTitle(title.trim());
        article.setContent(content.trim());
        article.setCategory(category.trim());
        article.setTags(payload.getOrDefault("tags", category).trim());
        return kbService.saveArticle(article);
    }

    @PostMapping("/reports/daily")
    public String generateDailyReport(@RequestParam String userId) {
        reportService.generateDailyReport(userId);
        return "Daily report generation triggered. Check console for details.";
    }

    @GetMapping(value = "/reports/daily/csv", produces = "text/csv")
    public ResponseEntity<String> exportDailyReport(@RequestParam String userId) {
        String csv = reportService.generateDailyReportCsv(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bpo-service-connect-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    private User requireCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userService.getRequiredUser(userId.toString());
    }

    private boolean isAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        String role = user.getRole().toLowerCase();
        return role.contains("admin") || role.contains("systemadmin");
    }

    private boolean isAgentOrLeaderOrAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        String role = user.getRole().toLowerCase();
        return role.contains("agent") || role.contains("leader") || role.contains("supervisor") || role.contains("admin");
    }
}
