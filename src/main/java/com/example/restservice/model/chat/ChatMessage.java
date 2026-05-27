package com.example.restservice.model.chat;

import com.example.restservice.model.user.Utilisateur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "sender") 
    private Utilisateur sender;

    @Column(columnDefinition="TEXT")
    private String content;
    private MessageType type;

    @Column(name = "timestamp")
    private Long timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage() {
    }

    public ChatMessage(Utilisateur sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }    

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getSender() {
        return sender;
    }

    public void setSender(Utilisateur sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
