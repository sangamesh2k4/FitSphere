package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.foodlog.*;
import com.sangamesh.Fitsphere.service.FoodLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Food Logs", description = "Manage daily nutrition logs")
@RestController
@RequestMapping("/api/foodlogs")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;

    @Operation(summary = "Log a new food")
    @PostMapping
    public ResponseEntity<FoodLogResponseDto> logFood(
            @Valid @RequestBody FoodLogRequestDto request) {

        return ResponseEntity.ok(
                foodLogService.logFood(request));
    }

    @Operation(summary = "get today food logs")
    @GetMapping("/today")
    public ResponseEntity<List<FoodLogResponseDto>> getTodayLogs() {

        return ResponseEntity.ok(
                foodLogService.getTodayLogs());
    }

    @Operation(summary = "get today food summary")
    @GetMapping("/today/summary")
    public ResponseEntity<DailyNutritionSummaryDto> getTodaySummary() {

        return ResponseEntity.ok(
                foodLogService.getTodaySummary());
    }

    @Operation(summary = "delete incorrect added food log ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodLog(@PathVariable Long id) {
        foodLogService.deleteFoodLog(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "view your food log history")
    @GetMapping("/history")
    public ResponseEntity<Page<DailyNutritionHistoryDto>> history(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(foodLogService.getNutritionHistory(startDate,endDate, page));
    }
    @Operation(summary = "view your food log history of a particular date")
    @GetMapping("/date")
    public ResponseEntity<List<FoodLogResponseDto>> getLogsByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(foodLogService.getLogsByDate(date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodLogResponseDto> updateFoodLog(@PathVariable Long id, @Valid @RequestBody FoodLogRequestDto request) {
        return ResponseEntity.ok(foodLogService.updateFoodLog(id, request));
    }
}
