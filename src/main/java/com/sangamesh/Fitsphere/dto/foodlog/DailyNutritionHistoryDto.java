package com.sangamesh.Fitsphere.dto.foodlog;


import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyNutritionHistoryDto {

    private LocalDate date;

    private Double totalCalories;

    private Double totalProtein;

    private Double totalCarbohydrates;

    private Double totalFat;

    private Integer mealCount;
}
