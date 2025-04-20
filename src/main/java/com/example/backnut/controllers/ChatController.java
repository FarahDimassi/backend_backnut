package com.example.backnut.controllers;

import com.example.backnut.models.Chat;
import com.example.backnut.models.ChatMessage;
import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.repository.ChatRepository;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Traite les messages entrants. Utilise l'ID de l'utilisateur authentifié comme senderId.
     * Vérifie qu'une invitation existe entre l'utilisateur (sender) et le coach (receiver)
     * avec le statut "ACCEPTED". Dans ce cas, sender doit être 9 et receiver 59.
     * Si la condition est remplie, le message est sauvegardé et diffusé dans la salle de chat.
     */
    @MessageMapping("/chat")
    public void processMessage(Principal principal, @Payload ChatMessage chatMessage) {
        // Extraire l'ID du sender depuis le Principal.
        Long senderId = Long.valueOf(principal.getName());
        chatMessage.setSenderId(senderId);

        // Récupérer les objets User pour sender et receiver.
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(chatMessage.getReceiverId());
        if (!senderOpt.isPresent() || !receiverOpt.isPresent()) {
            System.out.println("Sender or receiver not found.");
            return;
        }
        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        // Vérifier qu'une invitation ACCEPTED existe entre sender et receiver.
        // Dans votre scénario, vous attendez sender = 9 et receiver = 59.
        Optional<Invitation> invitationOpt = invitationRepository
                .findTopBySenderAndReceiverAndStatusOrderByIdDesc(sender, receiver, "ACCEPTED");

        // Si la requête ne renvoie rien, vous pouvez éventuellement tester dans l'autre sens.
        if (!invitationOpt.isPresent()) {
            invitationOpt = invitationRepository
                    .findTopBySenderAndReceiverAndStatusOrderByIdDesc(receiver, sender, "ACCEPTED");
        }
        if (!invitationOpt.isPresent()) {
            System.out.println("Aucune invitation ACCEPTED trouvée pour sender " + sender.getId() +
                    " et receiver " + receiver.getId());
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),  // Il faut que ce principal.getName() corresponde à l'identifiant de l'utilisateur côté client
                    "/queue/errors",
                    "Tu ne peux pas communiquer avec ce coach"
            );
            return;
        }

        // Si l'invitation est trouvée, traiter le message.
        Chat chat = new Chat(
                senderId,
                chatMessage.getReceiverId(),
                chatMessage.getMessage(),
                chatMessage.getAttachmentType(),  // "image" ou "audio", ou null
                chatMessage.getAttachmentUrl(),   // URL du fichier uploadé, ou null
                LocalDateTime.now()
        );
        chatRepository.save(chat);

        // Construire un identifiant de salle de chat (room id) de façon déterministe.
        String roomId = buildRoomId(senderId, chatMessage.getReceiverId());

        // Publier le message sur le topic de la salle.
        messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
    }

    /**
     * Construit un identifiant de salle à partir de deux identifiants, indépendamment de l'ordre.
     */
    private String buildRoomId(Long id1, Long id2) {
        return id1 < id2 ? id1 + "_" + id2 : id2 + "_" + id1;
    }
}
