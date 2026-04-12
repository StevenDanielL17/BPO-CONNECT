package com.bpoconnect.service;

import com.bpoconnect.model.FollowUpReminder;
import com.bpoconnect.repository.FollowUpReminderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReminderService {

    private final FollowUpReminderRepository reminderRepository;
    private final AuditService auditService;

    public ReminderService(FollowUpReminderRepository reminderRepository, AuditService auditService) {
        this.reminderRepository = reminderRepository;
        this.auditService = auditService;
    }

    public FollowUpReminder createReminder(String ticketId, String agentId, String reminderAtValue, String notes) {
        LocalDateTime reminderAt = parseReminderTime(reminderAtValue);
        FollowUpReminder reminder = new FollowUpReminder(
                "REM-" + UUID.randomUUID().toString().substring(0, 8),
                Objects.requireNonNull(ticketId, "ticketId"),
                Objects.requireNonNull(agentId, "agentId"),
                reminderAt,
                notes
        );
        FollowUpReminder savedReminder = reminderRepository.save(reminder);
        auditService.log(agentId, ticketId, "REMINDER_CREATED", "Reminder set for " + reminderAt);
        return savedReminder;
    }

    public List<FollowUpReminder> getRemindersForAgent(String agentId) {
        return reminderRepository.findByAgentIdOrderByReminderAtAsc(agentId);
    }

    public List<FollowUpReminder> getOpenReminders() {
        return reminderRepository.findByCompletedFalseOrderByReminderAtAsc();
    }

    public FollowUpReminder completeReminder(String reminderId, String completedBy) {
        FollowUpReminder reminder = reminderRepository.findById(Objects.requireNonNull(reminderId, "reminderId"))
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found."));
        reminder.setCompleted(true);
        reminder.setCompletedAt(LocalDateTime.now());
        FollowUpReminder savedReminder = reminderRepository.save(reminder);
        auditService.log(completedBy, reminder.getTicketId(), "REMINDER_COMPLETED", "Reminder " + reminderId + " marked complete");
        return savedReminder;
    }

    private LocalDateTime parseReminderTime(String reminderAtValue) {
        if (reminderAtValue == null || reminderAtValue.isBlank()) {
            return LocalDateTime.now().plusHours(1);
        }

        try {
            return LocalDateTime.parse(reminderAtValue);
        } catch (DateTimeParseException exception) {
            return LocalDateTime.now().plusHours(1);
        }
    }
}