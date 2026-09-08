package com.sangamesh.Fitsphere.dto.health;

import com.sangamesh.Fitsphere.dto.profile.MacroRecommendationDto;
import lombok.Data;

import java.util.List;

@Data
public class HealthAssessmentResponseDto {

    private Double bmi;
    private String bmiCategory;

    private Double bodyFatPercentage;
    private String bodyFatCategory;

    private Double bmr;
    private Double tdee;
    private Double recommendedCalories;

    private MacroRecommendationDto recommendedMacros;

    private Double recommendedWater;

    private Integer healthScore;

    private List<String> recommendations;

    private String healthyWeightRange;
    private String idealBodyFatRange;
    private String calorieGoalSummary;
}