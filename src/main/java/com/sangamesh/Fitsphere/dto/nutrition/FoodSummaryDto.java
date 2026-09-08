package com.sangamesh.Fitsphere.dto.nutrition;

import lombok.Data;

@Data
public class FoodSummaryDto {

    private Long fdcId;

    private String description;

    private String brandName;

    private String foodCategory;

    private Double servingSize;

    private String servingSizeUnit;

    private Double calories;

    private Double protein;

    private Double carbohydrates;

    private Double fat;
}