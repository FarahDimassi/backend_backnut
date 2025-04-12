package com.example.backnut.services;

import com.example.backnut.models.Conversation;
import com.example.backnut.models.Message;
import com.example.backnut.models.User;
import com.example.backnut.repository.ConversationRepository;
import com.example.backnut.repository.MessageRepository;
import com.example.backnut.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère ou crée une conversation entre un utilisateur et un coach.
     * @param userId l'ID de l'utilisateur
     * @param coachId l'ID du coach
     * @return la conversation existante ou nouvellement créée
     */
    public Conversation getOrCreateConversation(Long userId, Long coachId) {
        // Vérifier si la conversation existe déjà entre l'utilisateur et le coach
        return conversationRepository.findByUserIdAndCoachId(userId, coachId)
                .orElseGet(() -> {
                    // Si la conversation n'existe pas, créer une nouvelle conversation
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Utilisateur avec l'ID " + userId + " introuvable."));
                    User coach = userRepository.findById(coachId)
                            .orElseThrow(() -> new RuntimeException("Coach avec l'ID " + coachId + " introuvable."));

                    Conversation conversation = new Conversation();
                    conversation.setUser(user);
                    conversation.setCoach(coach);
                    return conversationRepository.save(conversation);
                });
    }

    /**
     * Envoie un message dans une conversation existante.
     * @param conversationId l'ID de la conversation
     * @param senderId l'ID de l'expéditeur
     * @param content le contenu du message
     * @return le message créé et sauvegardé
     */
    public Message sendMessage(Long conversationId, Long senderId, String content) {
        // Vérifier si la conversation existe
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation avec l'ID " + conversationId + " introuvable."));

        // Vérifier si l'expéditeur existe
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur avec l'ID " + senderId + " introuvable."));

        // Vérifier si l'expéditeur fait partie de cette conversation
        if (!(conversation.getUser().getId().equals(senderId) || conversation.getCoach().getId().equals(senderId))) {
            throw new RuntimeException("L'expéditeur ne fait pas partie de cette conversation.");
        }

        // Créer un nouveau message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);

        // Sauvegarde et retourne le message
        return messageRepository.save(message);
    }


    /**
     * Récupère la liste des messages d'une conversation dans l'ordre d'envoi.
     * @param conversationId l'ID de la conversation
     * @return la liste de tous les messages classés par date d'envoi
     */
    public List<Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
    }
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUser_Id(userId);
    }

    /**
     * Récupère la liste des conversations d'un coach.
     */
    public List<Conversation> getCoachConversations(Long coachId) {
        return conversationRepository.findByCoach_Id(coachId);
    }
}
