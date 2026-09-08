package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.nutrition.*;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ExternalApiException;
import com.sangamesh.Fitsphere.mapper.NutritionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionServiceImplTest {

    @Mock
    private USDAClient usdaClient;

    @Mock
    private NutritionMapper nutritionMapper;

    @InjectMocks
    private NutritionServiceImpl nutritionService;

    @Test
    void searchFoods_returnsMappedResponse() {

        USDAFoodSearchResponse usdaResponse = new USDAFoodSearchResponse();
        FoodSearchResponseDto expected = new FoodSearchResponseDto();

        when(usdaClient.searchFoods("chicken", 1))
                .thenReturn(usdaResponse);

        when(nutritionMapper.toFoodSearchResponseDto(usdaResponse))
                .thenReturn(expected);

        FoodSearchResponseDto result =
                nutritionService.searchFoods("chicken", 1);

        assertSame(expected, result);

        verify(usdaClient).searchFoods("chicken", 1);
        verify(nutritionMapper).toFoodSearchResponseDto(usdaResponse);
    }

    @Test
    void searchFoods_rejectsNullQuery() {

        assertThrows(
                BadRequestException.class,
                () -> nutritionService.searchFoods(null, 1)
        );

        verifyNoInteractions(usdaClient, nutritionMapper);
    }

    @Test
    void searchFoods_rejectsBlankQuery() {

        assertThrows(
                BadRequestException.class,
                () -> nutritionService.searchFoods("   ", 1)
        );

        verifyNoInteractions(usdaClient, nutritionMapper);
    }

    @Test
    void searchFoods_rejectsPageLessThanOne() {

        assertThrows(
                BadRequestException.class,
                () -> nutritionService.searchFoods("chicken", 0)
        );

        verifyNoInteractions(usdaClient, nutritionMapper);
    }

    @Test
    void searchFoods_wrapsRestClientException() {

        when(usdaClient.searchFoods("chicken", 1))
                .thenThrow(new RestClientException("USDA unavailable"));

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> nutritionService.searchFoods("chicken", 1)
        );

        assertEquals(
                "Unable to fetch food data from USDA.",
                exception.getMessage()
        );

        verify(usdaClient).searchFoods("chicken", 1);
        verifyNoInteractions(nutritionMapper);
    }

    @Test
    void getFoodDetails_returnsMappedFood() {

        USDAFoodDto usdaFood = new USDAFoodDto();
        FoodDetailDto expected = new FoodDetailDto();

        when(usdaClient.getFoodDetails(123L))
                .thenReturn(usdaFood);

        when(nutritionMapper.toFoodDetailDto(usdaFood))
                .thenReturn(expected);

        FoodDetailDto result =
                nutritionService.getFoodDetails(123L);

        assertSame(expected, result);

        verify(usdaClient).getFoodDetails(123L);
        verify(nutritionMapper).toFoodDetailDto(usdaFood);
    }

    @Test
    void calculateMacros_calculatesScaledMacros() {

        FoodDetailDto food = new FoodDetailDto();

        food.setFdcId(123L);
        food.setDescription("Chicken Breast");
        food.setServingSize(100.0);

        food.setCalories(200.0);
        food.setProtein(30.0);
        food.setCarbohydrates(5.0);
        food.setFat(4.0);
        food.setFiber(2.0);
        food.setSugar(1.0);
        food.setSodium(100.0);
        food.setPotassium(300.0);
        food.setCalcium(20.0);
        food.setIron(1.5);

        when(usdaClient.getFoodDetails(123L))
                .thenReturn(new USDAFoodDto());

        when(nutritionMapper.toFoodDetailDto(any(USDAFoodDto.class)))
                .thenReturn(food);

        MacroCalculationRequestDto request =
                new MacroCalculationRequestDto();

        request.setFdcId(123L);
        request.setQuantity(200.0);
        request.setUnit("g");

        MacroCalculationResponseDto result =
                nutritionService.calculateMacros(request);

        assertEquals(123L, result.getFdcId());
        assertEquals("Chicken Breast", result.getFoodName());
        assertEquals(200.0, result.getQuantity());
        assertEquals("g", result.getUnit());

        assertEquals(400.0, result.getCalories());
        assertEquals(60.0, result.getProtein());
        assertEquals(10.0, result.getCarbohydrates());
        assertEquals(8.0, result.getFat());
        assertEquals(4.0, result.getFiber());
        assertEquals(2.0, result.getSugar());
        assertEquals(200.0, result.getSodium());
        assertEquals(600.0, result.getPotassium());
        assertEquals(40.0, result.getCalcium());
        assertEquals(3.0, result.getIron());

        verify(usdaClient).getFoodDetails(123L);
        verify(nutritionMapper).toFoodDetailDto(any(USDAFoodDto.class));
    }

    @Test
    void calculateMacros_usesCorrectServingFactor() {

        FoodDetailDto food = new FoodDetailDto();

        food.setFdcId(10L);
        food.setDescription("Rice");
        food.setServingSize(50.0);

        food.setCalories(100.0);
        food.setProtein(2.0);
        food.setCarbohydrates(20.0);
        food.setFat(1.0);
        food.setFiber(1.0);
        food.setSugar(0.5);
        food.setSodium(10.0);
        food.setPotassium(50.0);
        food.setCalcium(5.0);
        food.setIron(0.5);

        when(usdaClient.getFoodDetails(10L))
                .thenReturn(new USDAFoodDto());

        when(nutritionMapper.toFoodDetailDto(any()))
                .thenReturn(food);

        MacroCalculationRequestDto request =
                new MacroCalculationRequestDto();

        request.setFdcId(10L);
        request.setQuantity(100.0);
        request.setUnit("g");

        MacroCalculationResponseDto result =
                nutritionService.calculateMacros(request);

        // 100g / 50g serving = factor 2
        assertEquals(200.0, result.getCalories());
        assertEquals(4.0, result.getProtein());
        assertEquals(40.0, result.getCarbohydrates());
        assertEquals(2.0, result.getFat());
        assertEquals(2.0, result.getFiber());
        assertEquals(1.0, result.getSugar());
        assertEquals(20.0, result.getSodium());
        assertEquals(100.0, result.getPotassium());
        assertEquals(10.0, result.getCalcium());
        assertEquals(1.0, result.getIron());
    }
}
