package com.example.backnut.controllers;

import com.example.backnut.models.Chat;
import com.example.backnut.models.ChatMessage;
import com.example.backnut.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    /**
     * Handle incoming chat messages.
     * Overrides the senderId with the authenticated user's id.
     * Publishes the message to a room identified as {min(senderId, receiverId)}_{max(senderId, receiverId)}
     * so that both sender and receiver receive the message.
     */
    @MessageMapping("/chat")
    public void processMessage(Principal principal, @Payload ChatMessage chatMessage) {
        // Use the authenticated user's id as senderId.
        Long senderId = Long.valueOf(principal.getName());
        chatMessage.setSenderId(senderId);

        // Persist the chat message.
        Chat chat = new Chat(
                senderId,
                chatMessage.getReceiverId(),
                chatMessage.getMessage(),
                LocalDateTime.now()
        );
        chatRepository.save(chat);

        // Build room id using a consistent order so that both parties join the same room.
        String roomId = buildRoomId(senderId, chatMessage.getReceiverId());

        // Publish the message to the room. Both sender and receiver should subscribe to this topic.
        messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
    }

    /**
     * Build a room id so that it is independent of sender/receiver order.
     */
    private String buildRoomId(Long id1, Long id2) {
        return id1 < id2 ? id1 + "_" + id2 : id2 + "_" + id1;
    }
}
