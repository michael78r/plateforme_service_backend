package com.example.restservice.controller.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.restservice.model.chat.ChatMessage;
// import com.example.restservice.model.user.Utilisateur;
// import com.example.restservice.repository.chat.ChatMessageRepository;
// import com.example.restservice.repository.user.UtilisateurRepository;

@Controller
public class ChatController {

    // @Autowired
    // private ChatMessageRepository chatRepo;
    // @Autowired
    // private UtilisateurRepository userRepo;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        // message.setContent(message.getSender() + " joined the chat");
        return message;
    }

    // @MessageMapping("/chat.sendMessage")
    // @SendTo("/topic/public")
    // public Map<String, Object> sendMessage(@Payload Map<String, Object> payload,
    // Principal principal) {
    // String email = principal.getName();
    // Utilisateur user = userRepo.findByEmail(email).orElseThrow(() -> new
    // RuntimeException("User not found"));

    // ChatMessage message = new ChatMessage();
    // message.setSender(user);
    // message.setContent((String) payload.get("content"));
    // chatRepo.save(message);

    // return Map.of(
    // "sender", user.getPrenom() + " " + user.getNom(),
    // "content", message.getContent(),
    // "timestamp", message.getId() // Using ID as a simple timestamp proxy
    // );
    // }

}
