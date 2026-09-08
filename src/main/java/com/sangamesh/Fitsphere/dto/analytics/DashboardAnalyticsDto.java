package com.sangamesh.Fitsphere.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardAnalyticsDto {

    private Double currentWeight;
    private Double weightChange;

    private Double currentBodyFatPercentage;
    private Double bodyFatChange;

    private Double averageDailyCalories;
    private Double recommendedCalories;

    private Double averageDailyProtein;
    private Double recommendedProtein;

    private Double calorieTargetPercentage;
    private Double proteinTargetPercentage;

    private Long daysLogged;
}
