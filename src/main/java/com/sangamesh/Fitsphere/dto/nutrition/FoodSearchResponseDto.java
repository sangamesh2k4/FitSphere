package com.sangamesh.Fitsphere.dto.nutrition;

import lombok.*;

import java.util.List;

@Data
public class FoodSearchResponseDto {

    private List<FoodSummaryDto> foods;
}