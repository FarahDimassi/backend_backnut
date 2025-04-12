package com.example.backnut.controllers;

import com.example.backnut.models.Review;
import com.example.backnut.security.JwtUtil;
import com.example.backnut.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtUtil jwtUtil;

    public ReviewController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
        // 1) Extraire userId du token
        Long userId = jwtUtil.extractUserId(token);
        // 2) Appeler le service
        Review createdReview = reviewService.createReview(review, userId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    // Mettre à jour une review
    @PutMapping("/{userId}/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long userId,
                                               @PathVariable Long reviewId,
                                               @RequestBody Review reviewDetails,
                                               @RequestHeader("Authorization") String token) {
        // Vérifier que userId dans l'URL == userId du token
        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!userId.equals(tokenUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Récupérer l'avis existant pour conserver le coachId
        Review existingReview = reviewService.getReviewById(reviewId);
        // Si le body ne fournit pas de coachId, on force celui existant
        if (reviewDetails.getCoachId() == null) {
            reviewDetails.setCoachId(existingReview.getCoachId());
        }
        Review updatedReview = reviewService.updateReview(userId, reviewId, reviewDetails);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }


    // Supprimer une review
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