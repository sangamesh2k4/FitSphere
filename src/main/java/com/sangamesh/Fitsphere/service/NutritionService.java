package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.nutrition.FoodDetailDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSearchResponseDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationRequestDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationResponseDto;

public interface NutritionService {

    FoodSearchResponseDto searchFoods(String query,int page);
    FoodDetailDto getFoodDetails(Long fdcId);
    MacroCalculationResponseDto calculateMacros(MacroCalculationRequestDto request);
}
