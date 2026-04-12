package com.bpoconnect.service;

import com.bpoconnect.model.AuditLog;
import com.bpoconnect.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

	private final AuditLogRepository auditLogRepository;

	public AuditService(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	public AuditLog log(String userId, String ticketId, String action, String detail) {
		AuditLog entry = new AuditLog(
				"AUD-" + UUID.randomUUID().toString().substring(0, 8),
				userId,
				ticketId,
				action,
				detail
		);
		return auditLogRepository.save(entry);
	}

	public List<AuditLog> getRecentLogs(int limit) {
		List<AuditLog> logs = auditLogRepository.findAllByOrderByCreatedAtDesc();
		if (limit <= 0 || logs.size() <= limit) {
			return logs;
		}
		return logs.subList(0, limit);
	}

	public List<AuditLog> getAllLogs() {
		return auditLogRepository.findAllByOrderByCreatedAtDesc();
	}

	public List<AuditLog> getTicketLogs(String ticketId) {
		return auditLogRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
	}
}
