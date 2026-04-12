package com.bpoconnect.service;

import com.bpoconnect.model.Ticket;
import com.bpoconnect.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueService {

	private final TicketRepository ticketRepository;

	public QueueService(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public int getWaitingCount() {
		return (int) getActiveQueue().stream()
				.filter(ticket -> isWaitingStatus(ticket.getStatus()))
				.count();
	}

	public void addToQueue() {
	}

	public void removeFromQueue() {
	}

	public Ticket getNextTicket() {
		return getActiveQueue().stream()
				.findFirst()
				.orElse(null);
	}

	public List<Ticket> getActiveQueue() {
		return ticketRepository.findAll().stream()
				.filter(ticket -> !"Resolved".equalsIgnoreCase(ticket.getStatus()) && !"Closed".equalsIgnoreCase(ticket.getStatus()))
				.sorted(Comparator.comparing(Ticket::getTicketId, Comparator.nullsLast(String::compareToIgnoreCase)))
				.collect(Collectors.toList());
	}

	public Map<String, Object> getRealTimeStats() {
		List<Ticket> activeQueue = getActiveQueue();
		Map<String, Object> stats = new LinkedHashMap<>();
		stats.put("waitingCount", getWaitingCount());
		stats.put("openCount", (int) activeQueue.stream().filter(ticket -> isWaitingStatus(ticket.getStatus())).count());
		stats.put("inProgressCount", (int) activeQueue.stream().filter(ticket -> "InProgress".equalsIgnoreCase(ticket.getStatus())).count());
		stats.put("escalatedCount", (int) activeQueue.stream().filter(ticket -> "Escalated".equalsIgnoreCase(ticket.getStatus())).count());
		stats.put("activeTickets", activeQueue);
		stats.put("byChannel", activeQueue.stream().collect(Collectors.groupingBy(ticket -> normalizeValue(ticket.getChannel()), Collectors.counting())));
		stats.put("bySeverity", activeQueue.stream().collect(Collectors.groupingBy(ticket -> normalizeValue(ticket.getSeverity()), Collectors.counting())));
		stats.put("nextTicket", getNextTicket());
		return stats;
	}

	private boolean isWaitingStatus(String status) {
		return "New".equalsIgnoreCase(status) || "Open".equalsIgnoreCase(status) || "InProgress".equalsIgnoreCase(status) || "PendingCustomer".equalsIgnoreCase(status);
	}

	private String normalizeValue(String value) {
		return value == null || value.isBlank() ? "General" : value;
	}
}
