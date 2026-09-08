package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.foodlog.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface FoodLogService {

    FoodLogResponseDto logFood(FoodLogRequestDto request);

    List<FoodLogResponseDto> getTodayLogs();
//
    DailyNutritionSummaryDto getTodaySummary();
//
    void deleteFoodLog(Long id);

    Page<DailyNutritionHistoryDto> getNutritionHistory(LocalDate startDate, LocalDate endDate,int page);

    List<FoodLogResponseDto> getLogsByDate(LocalDate date);

    FoodLogResponseDto updateFoodLog(Long id, FoodLogRequestDto request);
}
