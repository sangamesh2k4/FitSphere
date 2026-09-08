package com.sangamesh.Fitsphere.dto.nutrition;

import com.sangamesh.Fitsphere.enums.NutritionUnit;
import lombok.*;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionCalculationResponse {

    private String foodName;

    private Double quantity;

    private NutritionUnit unit;

    private Double calories;

    private Double protein;

    private Double carbohydrates;

    private Double fat;

    private Double fiber;
}
