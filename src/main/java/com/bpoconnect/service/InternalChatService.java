package com.bpoconnect.service;

import com.bpoconnect.model.InternalChatMessage;
import com.bpoconnect.repository.InternalChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InternalChatService {

	private final InternalChatMessageRepository messageRepository;
	private final AuditService auditService;

	public InternalChatService(InternalChatMessageRepository messageRepository, AuditService auditService) {
		this.messageRepository = messageRepository;
		this.auditService = auditService;
	}

	public InternalChatMessage sendMessage(String senderId, String recipientId, String message) {
		InternalChatMessage chatMessage = new InternalChatMessage(
				"CHAT-" + UUID.randomUUID().toString().substring(0, 8),
				Objects.requireNonNull(senderId, "senderId"),
				Objects.requireNonNull(recipientId, "recipientId"),
				Objects.requireNonNull(message, "message")
		);
		InternalChatMessage savedMessage = messageRepository.save(chatMessage);
		auditService.log(senderId, null, "CHAT_SENT", "Sent to " + recipientId);
		return savedMessage;
	}

	public List<InternalChatMessage> getMessages(String userId) {
		return messageRepository.findAllByOrderByCreatedAtDesc().stream()
				.filter(message -> userId != null && (userId.equals(message.getSenderId()) || userId.equals(message.getRecipientId())))
				.collect(Collectors.toList());
	}

	public InternalChatMessage markAsRead(String messageId, String userId) {
		InternalChatMessage chatMessage = messageRepository.findById(Objects.requireNonNull(messageId, "messageId"))
				.orElseThrow(() -> new IllegalArgumentException("Message not found."));
		if (userId != null && userId.equals(chatMessage.getRecipientId())) {
			chatMessage.setReadAt(LocalDateTime.now());
		}
		return messageRepository.save(Objects.requireNonNull(chatMessage, "chatMessage"));
	}
}
