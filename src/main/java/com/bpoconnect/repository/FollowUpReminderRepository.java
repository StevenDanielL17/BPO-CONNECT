package com.bpoconnect.repository;

import com.bpoconnect.model.FollowUpReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowUpReminderRepository extends JpaRepository<FollowUpReminder, String> {
    List<FollowUpReminder> findByAgentIdOrderByReminderAtAsc(String agentId);
    List<FollowUpReminder> findByCompletedFalseOrderByReminderAtAsc();
}