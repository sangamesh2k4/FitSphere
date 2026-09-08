package com.sangamesh.Fitsphere.dto.usda;

import lombok.Data;

@Data
public class USDANutrientDto {

    private Long id;

    private String type;

    private USDANutrientInfoDto nutrient;

    private Double amount;
}
