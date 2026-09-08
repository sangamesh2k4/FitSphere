package com.sangamesh.Fitsphere.dto.foodlog;

import com.sangamesh.Fitsphere.enums.MealType;
import lombok.Data;

import java.time.*;

@Data
public class FoodLogResponseDto {

    private Long id;

    private Long fdcId;

    private String foodName;

    private Double quantity;

    private String unit;

    private MealType mealType;

    private Double calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;

    private LocalDate logDate;

    private LocalDateTime loggedAt;
}
