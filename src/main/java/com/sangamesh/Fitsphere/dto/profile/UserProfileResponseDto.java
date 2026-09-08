package com.sangamesh.Fitsphere.dto.profile;

import com.sangamesh.Fitsphere.enums.ActivityLevel;
import com.sangamesh.Fitsphere.enums.Gender;
import com.sangamesh.Fitsphere.enums.Goal;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileResponseDto {

    // User Information

    private Integer age;

    private Gender gender;

    private Double height;

    private Double weight;

    private ActivityLevel activityLevel;

    private Goal goal;

    // Health Metrics

    private Double bmi;

    private String bmiCategory;

    private Double bodyFatPercentage;
    private String bodyFatCategory;

    private Double bmr;

    private Double tdee;

    // Daily Targets

    private Double recommendedCalories;

    private Double recommendedProtein;

    private Double recommendedCarbohydrates;

    private Double recommendedFat;

    private Double recommendedWater;

    // Overall Assessment

    private Integer healthScore;

    private List<String> recommendations;


    private String healthStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
