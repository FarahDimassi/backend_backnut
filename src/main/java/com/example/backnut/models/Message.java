package com.example.backnut.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;  // L'expéditeur du message

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;  // La conversation à laquelle appartient ce message

    private LocalDateTime sentAt = LocalDateTime.now();

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    // Getters, setters, etc.
}

