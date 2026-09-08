package com.sangamesh.Fitsphere.entity;


import com.sangamesh.Fitsphere.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double height; // cm

    private Double weight; // kg

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    // -------- Calculated Fields --------

    private Double bmi;

    private String bmiCategory;


    private Double bodyFatPercentage;
    private String bodyFatCategory;

    private Double bmr;

    private Double tdee;

    private Double recommendedCalories;

    private Double recommendedProtein;

    private Double recommendedCarbohydrates;

    private Double recommendedFat;

    private Double recommendedWater;

    private Integer healthScore;

    @Column(length = 500)
    private String healthStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
