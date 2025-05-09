package com.example.backnut.services;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    /**
     * Récupère la liste des invitations ACCEPTED pour un utilisateur
     * où l'utilisateur intervient comme sender ou receiver.
     */
    public List<Invitation> findAcceptedInvitationsForUser(User user) {
        return invitationRepository.findAcceptedInvitationsForUser("ACCEPTED", user.getId());
    }

    /**
     * Extrait la liste des "coach" pour l'utilisateur donné.
     * Pour chaque invitation ACCEPTED, si l'utilisateur est le sender, alors le coach est le receiver, sinon c'est le sender.
     */
    public List<User> findAcceptedCoachesForUser(User user) {
        List<Invitation> invitations = findAcceptedInvitationsForUser(user);
        return invitations.stream()
                .map(inv -> {
                    if (inv.getSender().getId().equals(user.getId())) {
                        return inv.getReceiver();
                    } else {
                        return inv.getSender();
                    }
                })
                .distinct()  // Élimine les doublons si plusieurs invitations existent
                .collect(Collectors.toList());
    }
    /**
     * Récupère les invitations ACCEPTED pour un coach spécifique.
     * Le coach est identifié ici par l'objet User passé en paramètre, et on considère qu'il
     * est le "receiver" de l'invitation.
     *
     * @param coach L'objet User représentant le coach.
     * @return La liste des Invitations dont le status est "ACCEPTED" pour ce coach.
     */
    public List<Invitation> findAcceptedInvitationsForCoach(User coach) {
        // On utilise "ACCEPTED" comme valeur de status, à adapter si besoin.
        return invitationRepository.findAcceptedInvitationsForCoach("ACCEPTED", coach.getId());
    }
}
