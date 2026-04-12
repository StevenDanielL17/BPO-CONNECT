package com.bpoconnect.repository;

import com.bpoconnect.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByTicketIdOrderByCreatedAtDesc(String ticketId);
}