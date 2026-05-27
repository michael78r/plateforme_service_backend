package com.example.restservice.controller.chat;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.restservice.model.chat.ChatMessage;
import com.example.restservice.repository.chat.ChatMessageRepository;


@Controller
@RequestMapping("/api")
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("chat/history")
    public Optional<ChatMessage> getChatHistory(@RequestParam Long senderId) {
        return chatMessageRepository.findAllByOrderByTimestampDesc();
    }
    
}
