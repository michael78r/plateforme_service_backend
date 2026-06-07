package com.example.restservice.chat;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
