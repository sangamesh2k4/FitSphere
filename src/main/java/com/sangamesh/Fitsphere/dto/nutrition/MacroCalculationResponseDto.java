package com.sangamesh.Fitsphere.dto.nutrition;

import lombok.Data;

@Data
public class MacroCalculationResponseDto {

    private Long fdcId;
    private String foodName;

    private Double quantity;
    private String unit;

    private Double calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double sodium;
    private Double potassium;
    private Double calcium;
    private Double iron;
}