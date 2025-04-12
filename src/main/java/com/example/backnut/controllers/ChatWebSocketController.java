package com.example.backnut.controllers;

import com.example.backnut.models.Message;
import com.example.backnut.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Méthode qui écoute les messages envoyés sur /app/chat.sendMessage.
     * Diffuse les messages aux clients abonnés au topic de la conversation.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload) {
        Long conversationId = Long.valueOf(payload.get("conversationId").toString());
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        String content = payload.get("content").toString();

        // Sauvegarde du message via le service
        Message savedMessage = conversationService.sendMessage(conversationId, senderId, content);

        // Diffusion du message sur le topic de la conversation
        messagingTemplate.convertAndSend(
                "/topic/conversation." + conversationId,
                savedMessage
        );
    }
}
