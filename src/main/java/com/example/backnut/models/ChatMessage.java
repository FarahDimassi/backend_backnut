package com.example.backnut.models;

import java.time.LocalDateTime;

public class ChatMessage {

    private Long senderId;
    private Long receiverId;
    private String message;
    private String attachmentType;   // "image" ou "audio", ou null
    private String attachmentUrl;    // URL du fichier, ou null
    private LocalDateTime date;

    // Constructeur par défaut
    public ChatMessage() {}

    // Constructeur sans pièce jointe
    public ChatMessage(Long senderId, Long receiverId, String message, LocalDateTime date) {
        this.senderId      = senderId;
        this.receiverId    = receiverId;
        this.message       = message;
        this.attachmentType= null;
        this.attachmentUrl = null;
        this.date          = date;
    }

    // Constructeur avec pièce jointe
    public ChatMessage(Long senderId,
                       Long receiverId,
                       String message,
                       String attachmentType,
                       String attachmentUrl,
                       LocalDateTime date) {
        this.senderId       = senderId;
        this.receiverId     = receiverId;
        this.message        = message;
        this.attachmentType = attachmentType;
        this.attachmentUrl  = attachmentUrl;
        this.date           = date;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
