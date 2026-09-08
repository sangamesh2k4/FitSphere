package com.sangamesh.Fitsphere.dto.nutrition;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodDetailDto {

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

    private Double fiber;

    private Double sugar;

    private Double sodium;

    private Double potassium;

    private Double calcium;

    private Double iron;

}