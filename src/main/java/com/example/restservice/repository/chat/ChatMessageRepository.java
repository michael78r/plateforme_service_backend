package com.example.restservice.repository.chat;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restservice.model.chat.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    Optional<ChatMessage> findAllByOrderByTimestampDesc();

}
