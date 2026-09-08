package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.nutrition.*;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ExternalApiException;
import com.sangamesh.Fitsphere.mapper.NutritionMapper;
import com.sangamesh.Fitsphere.service.NutritionService;
import com.sangamesh.Fitsphere.util.NutritionCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final USDAClient usdaClient;
    private final NutritionMapper nutritionMapper;

    @Override
    public FoodSearchResponseDto searchFoods(String query,int page) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Search query cannot be empty.");
        }
        if (page < 1) {
            throw new BadRequestException("Page number must be at least 1.");
        }
        try {
            USDAFoodSearchResponse response = usdaClient.searchFoods(query,page);
            return nutritionMapper.toFoodSearchResponseDto(response);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Unable to fetch food data from USDA.");
        }
    }

    @Override
    public FoodDetailDto getFoodDetails(Long fdcId) {
        USDAFoodDto food = usdaClient.getFoodDetails(fdcId);
        FoodDetailDto dto = nutritionMapper.toFoodDetailDto(food);
        return dto;
    }




    @Override
    public MacroCalculationResponseDto calculateMacros(
            MacroCalculationRequestDto request) {

        FoodDetailDto food = getFoodDetails(request.getFdcId());
        double factor = NutritionCalculator.calculateFactor(food.getServingSize(), request.getQuantity());
        MacroCalculationResponseDto dto = new MacroCalculationResponseDto();
        dto.setFdcId(food.getFdcId());
        dto.setFoodName(food.getDescription());
        dto.setQuantity(request.getQuantity());
        dto.setUnit(request.getUnit());
        dto.setCalories(NutritionCalculator.scale(food.getCalories(), factor));
        dto.setProtein(NutritionCalculator.scale(food.getProtein(), factor));
        dto.setCarbohydrates(NutritionCalculator.scale(food.getCarbohydrates(), factor));
        dto.setFat(NutritionCalculator.scale(food.getFat(), factor));
        dto.setFiber(NutritionCalculator.scale(food.getFiber(), factor));
        dto.setSugar(NutritionCalculator.scale(food.getSugar(), factor));
        dto.setSodium(NutritionCalculator.scale(food.getSodium(), factor));
        dto.setPotassium(NutritionCalculator.scale(food.getPotassium(), factor));
        dto.setCalcium(NutritionCalculator.scale(food.getCalcium(), factor));
        dto.setIron(NutritionCalculator.scale(food.getIron(), factor));
        return dto;
    }
}
