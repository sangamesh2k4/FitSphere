package com.sangamesh.Fitsphere.dto.foodlog;


import com.sangamesh.Fitsphere.enums.MealType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FoodLogRequestDto {

    @NotNull
    private Long fdcId;

    @NotNull
    @Positive
    private Double quantity;

    @NotBlank
    private String unit;

    @NotNull
    private MealType mealType;
}
