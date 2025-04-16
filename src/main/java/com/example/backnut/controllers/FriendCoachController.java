package com.example.backnut.controllers;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.services.InvitationService;
import com.example.backnut.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:8081")  // Ajustez selon votre configuration
public class FriendCoachController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    /**
     * Récupère la liste des coachs (amis) pour l'utilisateur dont l'ID est passé en path.
     * Exemple d'appel : GET http://localhost:8080/api/friends/coaches/27
     */
    @GetMapping("/coaches/{userId}")
    public ResponseEntity<List<User>> getAcceptedCoachesForUser(@PathVariable Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        List<User> coaches = invitationService.findAcceptedCoachesForUser(user);
        return new ResponseEntity<>(coaches, HttpStatus.OK);
    }
    /**
     * Récupère la liste des invitations ACCEPTED pour le coach spécifié par son ID.
     * Exemple d'appel : GET http://localhost:8080/api/friends/coach-invitations/27
     *
     * @param coachId l'identifiant du coach.
     * @return La liste des invitations acceptées pour ce coach.
     */
    @GetMapping("/coach-invitations/{coachId}")
    public ResponseEntity<List<Invitation>> getAcceptedInvitationsForCoach(@PathVariable Long coachId) {
        Optional<User> coachOpt = userService.getUserById(coachId);
        if (coachOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User coach = coachOpt.get();
        List<Invitation> invitations = invitationService.findAcceptedInvitationsForCoach(coach);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

}
