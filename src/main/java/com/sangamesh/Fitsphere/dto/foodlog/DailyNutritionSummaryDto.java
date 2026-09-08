package com.sangamesh.Fitsphere.dto.foodlog;

import lombok.Data;

@Data
public class DailyNutritionSummaryDto {

    private Double targetCalories;
    private Double consumedCalories;
    private Double remainingCalories;

    private Double targetProtein;
    private Double consumedProtein;

    private Double targetCarbohydrates;
    private Double consumedCarbohydrates;

    private Double targetFat;
    private Double consumedFat;

    private Integer totalMeals;
}