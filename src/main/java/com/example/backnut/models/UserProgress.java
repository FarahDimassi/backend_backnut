package com.example.backnut.models;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_progress")
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coach_type", nullable = false)
    private String coachType;

    @Column(name = "initial_weight")
    private Double initialWeight;

    @Column(name = "current_weight")
    private Double currentWeight;

    @Column(name = "target_weight")
    private Double targetWeight;

    @Column(name = "height")
    private Double height;

    @Column(name = "waist_size")
    private Double waistSize;

    @Column(name = "hip_size")
    private Double hipSize;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "progress_date")
    private LocalDate date;

    // Getters & Setters

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCoachType() { return coachType; }
    public void setCoachType(String coachType) { this.coachType = coachType; }

    public Double getInitialWeight() { return initialWeight; }
    public void setInitialWeight(Double initialWeight) { this.initialWeight = initialWeight; }

    public Double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Double currentWeight) { this.currentWeight = currentWeight; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWaistSize() { return waistSize; }
    public void setWaistSize(Double waistSize) { this.waistSize = waistSize; }

    public Double getHipSize() { return hipSize; }
    public void setHipSize(Double hipSize) { this.hipSize = hipSize; }

    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }

    public Integer getSatisfactionRating() { return satisfactionRating; }
    public void setSatisfactionRating(Integer satisfactionRating) { this.satisfactionRating = satisfactionRating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
