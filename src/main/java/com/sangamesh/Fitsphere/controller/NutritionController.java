package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.nutrition.FoodDetailDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationRequestDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationResponseDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import com.sangamesh.Fitsphere.service.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@Tag(name = "Nutrition", description = "Food search, macro calculation and nutrition tracking APIs")
public class NutritionController {

    private final USDAClient usdaClient;
    private final NutritionService nutritionService;

    @Operation(summary = "Search foods", description = "Search foods from the USDA FoodData Central database."
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Foods retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "500", description = "USDA service unavailable")
    })
    @GetMapping("/search")
    public USDAFoodSearchResponse searchFoods(@RequestParam String query,@RequestParam(defaultValue = "1") int page) {
        return usdaClient.searchFoods(query,page);
    }

    @GetMapping("/{fdcId}")
    public ResponseEntity<FoodDetailDto> getFoodDetails(
            @PathVariable Long fdcId) {

        return ResponseEntity.ok(
                nutritionService.getFoodDetails(fdcId)
        );
    }

    @PostMapping("/calculate")
    public ResponseEntity<MacroCalculationResponseDto> calculateMacros(
            @RequestBody @Valid MacroCalculationRequestDto request) {

        return ResponseEntity.ok(
                nutritionService.calculateMacros(request)
        );
    }
}
