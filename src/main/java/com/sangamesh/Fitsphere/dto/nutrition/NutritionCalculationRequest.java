package com.sangamesh.Fitsphere.dto.nutrition;

import com.sangamesh.Fitsphere.enums.NutritionUnit;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionCalculationRequest {

    private Long fdcId;

    private Double quantity;

    private NutritionUnit unit;
}