package com.example.backnut.controllers;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.Review;
import com.example.backnut.models.User;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import com.example.backnut.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ReviewController(JwtUtil jwtUtil, UserRepository userRepository, InvitationRepository invitationRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Review>> getReviewsByCoachId(@PathVariable Long coachId) {
        List<Review> reviews = reviewService.getReviewsByCoachId(coachId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review,
                                               @RequestHeader("Authorization") String token) {
        // Nettoyer le token s'il commence par "Bearer "
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Extraire l'ID utilisateur depuis le token (sender doit être l'utilisateur, ici par exemple ID=9)
        Long userId = jwtUtil.extractUserId(token);

        // Récupérer l'utilisateur (sender) et le coach (receiver) concernés
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        User coach = userRepository.findById(review.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach non trouvé"));

        // Vérifier qu'une invitation existe entre le sender (user, ici ID=9) et le coach (receiver, ici par exemple 59)
        // et que son statut est "ACCEPTED"
        Optional<Invitation> optInvitation = invitationRepository.findFirstBySenderAndReceiver(sender, coach);
        if (!optInvitation.isPresent() || !"ACCEPTED".equalsIgnoreCase(optInvitation.get().getStatus())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Assigner l'ID utilisateur à la review pour garantir qu'il ne puisse pas être falsifié par le client
        review.setUserId(userId);

        // Appeler le service de création de review
        Review createdReview = reviewService.createReview(review, userId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long userId,
                                               @PathVariable Long reviewId,
                                               @RequestBody Review reviewDetails,
                                               @RequestHeader("Authorization") String token) {
        // Vérifier que l'ID utilisateur dans l'URL correspond à celui extrait du token
        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!userId.equals(tokenUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Récupérer l'avis existant pour conserver le coachId
        Review existingReview = reviewService.getReviewById(reviewId);
        if (reviewDetails.getCoachId() == null) {
            reviewDetails.setCoachId(existingReview.getCoachId());
        }
        Review updatedReview = reviewService.updateReview(userId, reviewId, reviewDetails);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long userId,
                                             @PathVariable Long reviewId,
                                             @RequestHeader("Authorization") String token) {
        // Vérifier que l'ID utilisateur dans l'URL correspond à celui extrait du token
        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!userId.equals(tokenUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        reviewService.deleteReview(userId, reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
