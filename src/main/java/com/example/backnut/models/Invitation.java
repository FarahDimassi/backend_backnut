package com.example.backnut.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'utilisateur qui envoie l'invitation
    @ManyToOne
    private User sender;

    // Le coach qui reçoit
    @ManyToOne
    private User receiver;

    // Statut : PENDING, ACCEPTED
    private String status;

    private String adminRequestMessage;

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getReceiver() {
        return receiver;
    }

    public Object getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminRequestMessage() {
        return adminRequestMessage;
    }

    public void setAdminRequestMessage(String adminRequestMessage) {
        this.adminRequestMessage = adminRequestMessage;
    }
}
