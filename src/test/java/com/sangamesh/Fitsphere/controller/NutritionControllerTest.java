package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.nutrition.FoodDetailDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationRequestDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationResponseDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.NutritionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NutritionController.class)
@AutoConfigureMockMvc(addFilters = false)
class NutritionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private USDAClient usdaClient;

    @MockitoBean
    private NutritionService nutritionService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void searchFoods_returnsSearchResponse() throws Exception {

        USDAFoodSearchResponse response = new USDAFoodSearchResponse();

        when(usdaClient.searchFoods("chicken", 1))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/nutrition/search")
                                .param("query", "chicken")
                )
                .andExpect(status().isOk());

        verify(usdaClient).searchFoods("chicken", 1);
    }


    @Test
    void searchFoods_usesProvidedPage() throws Exception {

        USDAFoodSearchResponse response = new USDAFoodSearchResponse();

        when(usdaClient.searchFoods("chicken", 3))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/nutrition/search")
                                .param("query", "chicken")
                                .param("page", "3")
                )
                .andExpect(status().isOk());

        verify(usdaClient).searchFoods("chicken", 3);
    }


    @Test
    void searchFoods_returnsBadRequestWhenQueryMissing() throws Exception {

        mockMvc.perform(
                        get("/api/nutrition/search")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usdaClient);
    }


    @Test
    void searchFoods_returnsBadRequestWhenPageInvalid() throws Exception {

        mockMvc.perform(
                        get("/api/nutrition/search")
                                .param("query", "chicken")
                                .param("page", "invalid")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usdaClient);
    }


    @Test
    void getFoodDetails_returnsFoodDetails() throws Exception {

        FoodDetailDto response = new FoodDetailDto();

        when(nutritionService.getFoodDetails(123L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/nutrition/123")
                )
                .andExpect(status().isOk());

        verify(nutritionService).getFoodDetails(123L);
    }


    @Test
    void getFoodDetails_returnsBadRequestWhenIdInvalid() throws Exception {

        mockMvc.perform(
                        get("/api/nutrition/invalid")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(nutritionService);
    }


    @Test
    void calculateMacros_returnsCalculatedMacros() throws Exception {

        MacroCalculationResponseDto response =
                new MacroCalculationResponseDto();

        when(nutritionService.calculateMacros(any(MacroCalculationRequestDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "fdcId": 123,
                    "quantity": 100,
                    "unit": "g"
                }
                """;

        mockMvc.perform(
                        post("/api/nutrition/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());

        verify(nutritionService)
                .calculateMacros(any(MacroCalculationRequestDto.class));
    }


    @Test
    void calculateMacros_returnsBadRequestWhenBodyMissing() throws Exception {

        mockMvc.perform(
                        post("/api/nutrition/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(nutritionService);
    }


    @Test
    void calculateMacros_returnsBadRequestWhenJsonInvalid() throws Exception {

        mockMvc.perform(
                        post("/api/nutrition/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(nutritionService);
    }


    @Test
    void unknownEndpoint_returnsNotFound() throws Exception {

        mockMvc.perform(
                        get("/api/nutrition/unknown/path")
                )
                .andExpect(status().isNotFound());
    }
}