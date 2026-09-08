package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.foodlog.DailyNutritionHistoryDto;
import com.sangamesh.Fitsphere.dto.foodlog.DailyNutritionSummaryDto;
import com.sangamesh.Fitsphere.dto.foodlog.FoodLogRequestDto;
import com.sangamesh.Fitsphere.dto.foodlog.FoodLogResponseDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationRequestDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationResponseDto;
import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.FoodLogMapper;
import com.sangamesh.Fitsphere.repository.FoodLogRepository;
import com.sangamesh.Fitsphere.repository.UserProfileRepository;
import com.sangamesh.Fitsphere.service.FoodLogService;
import com.sangamesh.Fitsphere.service.NutritionService;
import com.sangamesh.Fitsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodLogServiceImpl implements FoodLogService {

    private final FoodLogRepository foodLogRepository;

    private final FoodLogMapper foodLogMapper;

    private final NutritionService nutritionService;

    private final UserService userService;

    private final UserProfileRepository userProfileRepository;

    private static final int HISTORY_PAGE_SIZE = 12;

    @Override
    public FoodLogResponseDto logFood(FoodLogRequestDto request) {
        User user = userService.getCurrentUser();
        MacroCalculationRequestDto macroRequest = new MacroCalculationRequestDto();
        macroRequest.setFdcId(request.getFdcId());
        macroRequest.setQuantity(request.getQuantity());
        macroRequest.setUnit(request.getUnit());
        MacroCalculationResponseDto macros = nutritionService.calculateMacros(macroRequest);

        FoodLog foodLog = new FoodLog();
        foodLog.setUser(user);
        foodLog.setFdcId(macros.getFdcId());
        foodLog.setFoodName(macros.getFoodName());
        foodLog.setQuantity(request.getQuantity());
        foodLog.setUnit(request.getUnit());
        foodLog.setMealType(request.getMealType());
        foodLog.setCalories(macros.getCalories());
        foodLog.setProtein(macros.getProtein());
        foodLog.setCarbohydrates(macros.getCarbohydrates());
        foodLog.setFat(macros.getFat());
        foodLog.setFiber(macros.getFiber());
        foodLog.setSugar(macros.getSugar());
        foodLog.setSodium(macros.getSodium());
        foodLog.setLogDate(LocalDate.now());
        foodLog.setLoggedAt(LocalDateTime.now());
        foodLog = foodLogRepository.save(foodLog);

        return foodLogMapper.toResponse(foodLog);
    }

    @Override
    public List<FoodLogResponseDto> getTodayLogs() {
        User user = userService.getCurrentUser();
        List<FoodLog> logs = foodLogRepository.findByUserAndLogDate(user, LocalDate.now());
        return logs.stream().map(foodLogMapper::toResponse).toList();
    }

    @Override
    public DailyNutritionSummaryDto getTodaySummary() {
        User user = userService.getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<FoodLog> logs = foodLogRepository.findByUserAndLogDate(user, LocalDate.now());

        double calories = logs.stream().mapToDouble(FoodLog::getCalories).sum();
        double protein = logs.stream().mapToDouble(FoodLog::getProtein).sum();
        double carbs = logs.stream().mapToDouble(FoodLog::getCarbohydrates).sum();
        double fat = logs.stream().mapToDouble(FoodLog::getFat).sum();

        DailyNutritionSummaryDto dto = new DailyNutritionSummaryDto();

        dto.setTargetCalories(profile.getRecommendedCalories());
        dto.setConsumedCalories(calories);
        dto.setRemainingCalories(profile.getRecommendedCalories() - calories);

        dto.setTargetProtein(profile.getRecommendedProtein());
        dto.setConsumedProtein(protein);

        dto.setTargetCarbohydrates(profile.getRecommendedCarbohydrates());
        dto.setConsumedCarbohydrates(carbs);

        dto.setTargetFat(profile.getRecommendedFat());
        dto.setConsumedFat(fat);

        dto.setTotalMeals(logs.size());

        return dto;
    }

    @Override
    public void deleteFoodLog(Long foodLogId) {
        User user = userService.getCurrentUser();
        FoodLog foodLog = foodLogRepository.findById(foodLogId).orElseThrow(() ->
                new ResourceNotFoundException("Food log not found"));

        if (!foodLog.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Unauthorized");
        }
        foodLogRepository.delete(foodLog);
    }

    @Override
    public Page<DailyNutritionHistoryDto> getNutritionHistory(
            LocalDate startDate,
            LocalDate endDate,
            int page) {

        User user = userService.getCurrentUser();

        List<FoodLog> logs =
                foodLogRepository
                        .findByUserAndLogDateBetweenOrderByLogDateDesc(
                                user,
                                startDate,
                                endDate
                        );

        Map<LocalDate, List<FoodLog>> grouped =
                logs.stream()
                        .collect(Collectors.groupingBy(
                                FoodLog::getLogDate,
                                () -> new TreeMap<>(Comparator.reverseOrder()),
                                Collectors.toList()
                        ));

        List<DailyNutritionHistoryDto> history =
                new ArrayList<>();

        for (Map.Entry<LocalDate, List<FoodLog>> entry
                : grouped.entrySet()) {

            DailyNutritionHistoryDto dto =
                    new DailyNutritionHistoryDto();

            dto.setDate(entry.getKey());

            List<FoodLog> dayLogs = entry.getValue();

            dto.setTotalCalories(
                    dayLogs.stream()
                            .mapToDouble(FoodLog::getCalories)
                            .sum()
            );

            dto.setTotalProtein(
                    dayLogs.stream()
                            .mapToDouble(FoodLog::getProtein)
                            .sum()
            );

            dto.setTotalCarbohydrates(
                    dayLogs.stream()
                            .mapToDouble(FoodLog::getCarbohydrates)
                            .sum()
            );

            dto.setTotalFat(
                    dayLogs.stream()
                            .mapToDouble(FoodLog::getFat)
                            .sum()
            );

            dto.setMealCount(dayLogs.size());

            history.add(dto);
        }

        int totalElements = history.size();

        int start = page * HISTORY_PAGE_SIZE;

        int end = Math.min(
                start + HISTORY_PAGE_SIZE,
                totalElements
        );

        List<DailyNutritionHistoryDto> pageContent;

        if (start >= totalElements) {
            pageContent = List.of();
        } else {
            pageContent = history.subList(start, end);
        }

        return new PageImpl<>(
                pageContent,
                PageRequest.of(page, HISTORY_PAGE_SIZE),
                totalElements
        );
    }

    @Override
    public List<FoodLogResponseDto> getLogsByDate(LocalDate date) {
        User user = userService.getCurrentUser();
        List<FoodLog> logs = foodLogRepository.findByUserAndLogDate(user, date);
        return logs.stream().map(foodLogMapper::toResponse).toList();
    }

    @Override
    public FoodLogResponseDto updateFoodLog(Long id, FoodLogRequestDto request) {
        User user = userService.getCurrentUser();
        FoodLog foodLog = foodLogRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Food log not found"));
        if (!foodLog.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Unauthorized");
        }
        MacroCalculationRequestDto macroRequest = new MacroCalculationRequestDto();
        macroRequest.setFdcId(request.getFdcId());
        macroRequest.setQuantity(request.getQuantity());
        macroRequest.setUnit(request.getUnit());

        MacroCalculationResponseDto macros = nutritionService.calculateMacros(macroRequest);
        foodLog.setFdcId(macros.getFdcId());
        foodLog.setFoodName(macros.getFoodName());
        foodLog.setQuantity(request.getQuantity());
        foodLog.setUnit(request.getUnit());
        foodLog.setMealType(request.getMealType());
        foodLog.setCalories(macros.getCalories());
        foodLog.setProtein(macros.getProtein());
        foodLog.setCarbohydrates(macros.getCarbohydrates());
        foodLog.setFat(macros.getFat());
        foodLog.setFiber(macros.getFiber());
        foodLog.setSugar(macros.getSugar());
        foodLog.setSodium(macros.getSodium());
        foodLog = foodLogRepository.save(foodLog);
        return foodLogMapper.toResponse(foodLog);
    }
}