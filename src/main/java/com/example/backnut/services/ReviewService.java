package com.example.backnut.services;

import com.example.backnut.models.Review;
import com.example.backnut.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Créer une review pour un utilisateur donné (userId).
     * Le userId vient du token (extrait dans le controller).
     */
    public Review createReview(Review review, Long userId) {
        // On assigne l'ID de l'utilisateur (celui du token) à la review
        review.setUserId(userId);
        return reviewRepository.save(review);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByCoachId(Long coachId) {
        return reviewRepository.findByCoachId(coachId);
    }

    /**
     * Mettre à jour une review, en vérifiant que l'utilisateur est bien le propriétaire de la review.
     */
    public Review updateReview(Long userId, Long reviewId, Review reviewDetails) {
        Review existingReview = getReviewById(reviewId);
        // Vérification : la review doit appartenir à l'utilisateur qui effectue la requête
        if (!existingReview.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this review");
        }
        existingReview.setRating(reviewDetails.getRating());
        existingReview.setComment(reviewDetails.getComment());
        existingReview.setCoachId(reviewDetails.getCoachId());
        return reviewRepository.save(existingReview);
    }

    /**
     * Supprimer une review, en vérifiant que l'utilisateur est bien le propriétaire de la review.
     */
    public void deleteReview(Long userId, Long reviewId) {
        Review existingReview = getReviewById(reviewId);
        // Vérification : la review doit appartenir à l'utilisateur qui effectue la requête
        if (!existingReview.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this review");
        }
        reviewRepository.deleteById(reviewId);
    }
}
