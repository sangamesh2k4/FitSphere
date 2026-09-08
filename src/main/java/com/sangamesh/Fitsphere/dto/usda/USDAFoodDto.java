package com.sangamesh.Fitsphere.dto.usda;

import lombok.*;

import java.util.List;

@Data
public class USDAFoodDto {

    private Long fdcId;

    private String description;

    private String brandName;

    private String foodCategory;

    private Double servingSize;

    private String servingSizeUnit;

    private List<USDANutrientDto> foodNutrients;
}
