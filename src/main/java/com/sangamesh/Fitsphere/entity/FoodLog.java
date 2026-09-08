package com.sangamesh.Fitsphere.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sangamesh.Fitsphere.enums.MealType;
import lombok.Data;


@Data
@Entity
@Table(name = "food_logs")
public class FoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long fdcId;

    private String foodName;

    private Double quantity;

    private String unit;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    private Double calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double sodium;

    private LocalDate logDate;

    private LocalDateTime loggedAt;
}
