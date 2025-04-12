package com.example.backnut.controllers;


import com.example.backnut.models.Chat;
import com.example.backnut.models.ChatMessage;
import com.example.backnut.models.User;
import com.example.backnut.repository.ChatRepository;
import com.example.backnut.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/history/{receiverId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long receiverId, Principal principal) {
        String userName = principal.getName();
        if (userName == null || userName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<User> optionalUser = userRepository.findByUsername(userName);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = optionalUser.get().getId();
        List<Chat> chatList = chatRepository.findChatHistory(userId, receiverId);
        List<ChatMessage> chatMessages = chatList.stream()
                .map(chat -> new ChatMessage(
                        chat.getSenderId(),
                        chat.getReceiverId(),
                        chat.getMessage(),
                        chat.getDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatMessages);
    }
    @GetMapping("/get-username-by-id/{userId}")
    public ResponseEntity<?> getUsernameById(
            @PathVariable("userId") Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if(user.isPresent()){
                return ResponseEntity.ok(user.get().getUsername());
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de trouver le username par id : " + e.getMessage());
        }
    }
        @GetMapping("/contacts")
        public ResponseEntity<List<Map<String, Object>>> getContacts(
                @RequestParam(required = false) Long userId,
                @RequestParam(required = false) Long coachId) {
            // On utilise userId s'il est fourni, sinon coachId
            Long id = (userId != null) ? userId : coachId;
            if (id == null) {
                return ResponseEntity.badRequest().build();
            }

            // Récupère tous les messages où l'ID apparaît comme expéditeur ou destinataire
            List<Chat> chats = chatRepository.findChatsByUserId(id, id);

            // Groupement par l'ID du partenaire (si l'utilisateur est expéditeur, le partenaire est le destinataire, sinon inversement)
            Map<Long, List<Chat>> grouped = chats.stream().collect(Collectors.groupingBy(chat ->
                    chat.getSenderId().equals(id) ? chat.getReceiverId() : chat.getSenderId()
            ));

            List<Map<String, Object>> contacts = new ArrayList<>();

            // Pour chaque partenaire, on trie par date décroissante et on prend le dernier message
            for (Map.Entry<Long, List<Chat>> entry : grouped.entrySet()) {
                Long partnerId = entry.getKey();
                List<Chat> partnerChats = entry.getValue();
                partnerChats.sort((c1, c2) -> c2.getDate().compareTo(c1.getDate()));
                Chat latest = partnerChats.get(0);

                // Récupération des informations du partenaire
                Optional<User> partnerOpt = userRepository.findById(partnerId);
                String partnerName = partnerOpt.map(User::getUsername).orElse("Inconnu");
                String partnerImageUrl = partnerOpt.map(User::getPhotoUrl).orElse(null);

                // Construction d'une map contenant les informations du contact
                Map<String, Object> map = new HashMap<>();
                map.put("partnerId", partnerId);
                map.put("partnerName", partnerName);
                map.put("partnerImageUrl", partnerImageUrl);
                map.put("lastMessage", latest.getMessage());
                map.put("lastMessageTime", latest.getDate().toString()); // Formatez la date si nécessaire

                contacts.add(map);
            }

            // Tri des contacts par date du dernier message (le plus récent en premier)
            contacts.sort((m1, m2) -> ((String) m2.get("lastMessageTime")).compareTo((String) m1.get("lastMessageTime")));

            return ResponseEntity.ok(contacts);
        }
    }

