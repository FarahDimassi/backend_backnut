package com.example.backnut.controllers;

import com.example.backnut.models.Conversation;
import com.example.backnut.models.Message;
import com.example.backnut.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * Récupère ou crée une conversation entre un utilisateur et un coach.
     * @param userId l'ID de l'utilisateur
     * @param coachId l'ID du coach
     * @return la conversation (existe déjà ou nouvellement créée)
     */
    @PostMapping("/getOrCreate")
    public ResponseEntity<Conversation> getOrCreateConversation(@RequestParam Long userId,
                                                                @RequestParam Long coachId) {
        Conversation conversation = conversationService.getOrCreateConversation(userId, coachId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Envoie un message dans une conversation existante.
     * @param conversationId l'ID de la conversation
     * @param senderId l'ID de l'expéditeur
     * @param content le contenu du message
     * @return le message créé
     */
    @PostMapping("/{conversationId}/sendMessage")
    public ResponseEntity<Message> sendMessage(@PathVariable Long conversationId,
                                               @RequestParam Long senderId,
                                               @RequestParam String content) {
        Message message = conversationService.sendMessage(conversationId, senderId, content);
        return ResponseEntity.ok(message);
    }

    /**
     * Récupère l'historique des messages d'une conversation.
     * @param conversationId l'ID de la conversation
     * @return la liste de tous les messages classés par date d'envoi
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long conversationId) {
        // Vérification si l'ID de la conversation est valide
        if (conversationId == null || conversationId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Message> messages = conversationService.getMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable Long userId) {
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Récupère la liste des conversations pour un coach.
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Conversation>> getCoachConversations(@PathVariable Long coachId) {
        List<Conversation> conversations = conversationService.getCoachConversations(coachId);
        return ResponseEntity.ok(conversations);
    }
}
