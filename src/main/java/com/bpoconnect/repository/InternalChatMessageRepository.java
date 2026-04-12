package com.bpoconnect.repository;

import com.bpoconnect.model.InternalChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalChatMessageRepository extends JpaRepository<InternalChatMessage, String> {
    List<InternalChatMessage> findAllByOrderByCreatedAtDesc();
}