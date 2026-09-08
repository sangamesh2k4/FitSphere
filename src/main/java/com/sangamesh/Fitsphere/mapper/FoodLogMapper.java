package com.sangamesh.Fitsphere.mapper;

import com.sangamesh.Fitsphere.dto.foodlog.FoodLogResponseDto;
import com.sangamesh.Fitsphere.entity.FoodLog;
import org.springframework.stereotype.Component;

@Component
public class FoodLogMapper {

    public FoodLogResponseDto toResponse(FoodLog foodLog) {

        FoodLogResponseDto dto = new FoodLogResponseDto();

        dto.setId(foodLog.getId());

        dto.setFdcId(foodLog.getFdcId());
        dto.setFoodName(foodLog.getFoodName());

        dto.setQuantity(foodLog.getQuantity());
        dto.setUnit(foodLog.getUnit());

        dto.setMealType(foodLog.getMealType());

        dto.setCalories(foodLog.getCalories());
        dto.setProtein(foodLog.getProtein());
        dto.setCarbohydrates(foodLog.getCarbohydrates());
        dto.setFat(foodLog.getFat());

        dto.setLogDate(foodLog.getLogDate());
        dto.setLoggedAt(foodLog.getLoggedAt());

        return dto;
    }
}