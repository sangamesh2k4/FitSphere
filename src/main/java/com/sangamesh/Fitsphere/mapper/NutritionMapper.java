package com.sangamesh.Fitsphere.mapper;



import com.sangamesh.Fitsphere.constants.NutrientConstants;
import com.sangamesh.Fitsphere.dto.nutrition.FoodDetailDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSearchResponseDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import com.sangamesh.Fitsphere.dto.usda.USDANutrientDto;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class NutritionMapper{

    public FoodSummaryDto toFoodSummaryDto(USDAFoodDto food) {

    FoodSummaryDto dto = new FoodSummaryDto();

    dto.setFdcId(food.getFdcId());
    dto.setDescription(food.getDescription());
    dto.setBrandName(food.getBrandName());
    dto.setFoodCategory(food.getFoodCategory());
    dto.setServingSize(food.getServingSize());
    dto.setServingSizeUnit(food.getServingSizeUnit());

        dto.setCalories(getNutrientValue(food, NutrientConstants.ENERGY));
        dto.setProtein(getNutrientValue(food, NutrientConstants.PROTEIN));
        dto.setCarbohydrates(getNutrientValue(food, NutrientConstants.CARBOHYDRATE));
        dto.setFat(getNutrientValue(food, NutrientConstants.FAT));

    return dto;
}
    private Double getNutrientValue(USDAFoodDto food, String nutrientName) {

        if (food.getFoodNutrients() == null) {
            return 0.0;
        }

        return food.getFoodNutrients()
                .stream()
                .filter(n ->
                        n.getNutrient() != null &&
                                nutrientName.equalsIgnoreCase(n.getNutrient().getName()))
                .map(USDANutrientDto::getAmount)
                .findFirst()
                .orElse(0.0);
    }

    public FoodSearchResponseDto toFoodSearchResponseDto(USDAFoodSearchResponse response) {

        FoodSearchResponseDto dto = new FoodSearchResponseDto();
        List<USDAFoodDto> foods = Optional.ofNullable(response.getFoods()).orElse(Collections.emptyList());
        dto.setFoods(
                foods.stream()
                        .map(this::toFoodSummaryDto)
                        .toList()
        );

        return dto;
    }


    public FoodDetailDto toFoodDetailDto(USDAFoodDto food) {

        FoodDetailDto dto = new FoodDetailDto();

        dto.setFdcId(food.getFdcId());
        dto.setDescription(food.getDescription());
        dto.setBrandName(food.getBrandName());
        dto.setFoodCategory(food.getFoodCategory());

        dto.setServingSize(food.getServingSize());
        dto.setServingSizeUnit(food.getServingSizeUnit());

        dto.setCalories(getNutrientValue(food, NutrientConstants.ENERGY));
        dto.setProtein(getNutrientValue(food, NutrientConstants.PROTEIN));
        dto.setCarbohydrates(getNutrientValue(food, NutrientConstants.CARBOHYDRATE));
        dto.setFat(getNutrientValue(food, NutrientConstants.FAT));

        dto.setFiber(getNutrientValue(food, NutrientConstants.FIBER));
        dto.setSugar(getNutrientValue(food, NutrientConstants.SUGAR));
        dto.setSodium(getNutrientValue(food, NutrientConstants.SODIUM));
        dto.setPotassium(getNutrientValue(food, NutrientConstants.POTASSIUM));
        dto.setCalcium(getNutrientValue(food, NutrientConstants.CALCIUM));
        dto.setIron(getNutrientValue(food, NutrientConstants.IRON));

        return dto;
    }
}