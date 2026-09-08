package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.analytics.DashboardAnalyticsDto;
import com.sangamesh.Fitsphere.entity.BodyMeasurement;
import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import com.sangamesh.Fitsphere.repository.BodyMeasurementRepository;
import com.sangamesh.Fitsphere.repository.FoodLogRepository;
import com.sangamesh.Fitsphere.repository.UserProfileRepository;
import com.sangamesh.Fitsphere.service.AnalyticsService;
import com.sangamesh.Fitsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;
    private final FoodLogRepository foodLogRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;

    @Override
    public DashboardAnalyticsDto getDashboardAnalytics() {
        User user = userService.getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

        List<FoodLog> weeklyFoodLogs = foodLogRepository.findByUserAndLogDateBetweenOrderByLogDateDesc(
                        user, startOfWeek, today);
        List<BodyMeasurement> measurements = bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtDescIdDesc(user.getId());
        return buildDashboard(profile, weeklyFoodLogs, measurements);
    }
    private DashboardAnalyticsDto buildDashboard(UserProfile profile, List<FoodLog> foodLogs, List<BodyMeasurement> measurements) {
        long daysLogged = foodLogs.stream()
                .map(FoodLog::getLogDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        double totalCalories = foodLogs.stream()
                .map(FoodLog::getCalories)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalProtein = foodLogs.stream()
                .map(FoodLog::getProtein)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double averageDailyCalories = daysLogged == 0 ? 0 : totalCalories / daysLogged;
        double averageDailyProtein = daysLogged == 0 ? 0 : totalProtein / daysLogged;
        Double currentWeight = getCurrentWeight(measurements, profile);
        Double weightChange = getWeightChange(measurements);
        Double currentBodyFat = getCurrentBodyFat(measurements, profile);
        Double bodyFatChange = getBodyFatChange(measurements);
        Double recommendedCalories = profile != null ? profile.getRecommendedCalories() : null;
        Double recommendedProtein = profile != null ? profile.getRecommendedProtein() : null;
        Double calorieTarget = calculateTarget(averageDailyCalories, recommendedCalories);
        Double proteinTarget = calculateTarget(averageDailyProtein, recommendedProtein);
        return DashboardAnalyticsDto.builder()
                .currentWeight(round(currentWeight))
                .weightChange(round(weightChange))
                .currentBodyFatPercentage(round(currentBodyFat))
                .bodyFatChange(round(bodyFatChange))
                .averageDailyCalories(round(averageDailyCalories))
                .recommendedCalories(recommendedCalories)
                .averageDailyProtein(round(averageDailyProtein))
                .recommendedProtein(recommendedProtein)
                .calorieTargetPercentage(round(calorieTarget))
                .proteinTargetPercentage(round(proteinTarget))
                .daysLogged(daysLogged)
                .build();
    }

    // helpers
    private Double getCurrentWeight(List<BodyMeasurement> measurements, UserProfile profile) {
        Double measurementWeight = measurements.stream()
                .map(BodyMeasurement::getWeight)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (measurementWeight != null) {
            return measurementWeight;
        }
        return profile != null
                ? profile.getWeight()
                : null;
    }
    private Double getWeightChange(List<BodyMeasurement> measurements) {
        List<Double> weights = measurements.stream()
                .map(BodyMeasurement::getWeight)
                .filter(Objects::nonNull)
                .toList();

        if (weights.size() < 2) {
            return 0.0;
        }

        Double currentWeight = weights.get(0);
        Double previousWeight = weights.get(1);
        return currentWeight - previousWeight;
    }

    private Double getCurrentBodyFat(
            List<BodyMeasurement> measurements,
            UserProfile profile) {

        Double measurementBodyFat = measurements.stream()
                .map(BodyMeasurement::getBodyFatPercentage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (measurementBodyFat != null) {
            return measurementBodyFat;
        }

        return profile != null
                ? profile.getBodyFatPercentage()
                : null;
    }

    private Double getBodyFatChange(List<BodyMeasurement> measurements) {

        List<Double> bodyFatValues = measurements.stream()
                .map(BodyMeasurement::getBodyFatPercentage)
                .filter(Objects::nonNull)
                .toList();

        if (bodyFatValues.size() < 2) {
            return 0.0;
        }

        return bodyFatValues.get(0) - bodyFatValues.get(1);
    }

    private Double calculateTarget(double actual, Double recommended) {
        if (recommended == null || recommended <= 0) {
            return 0.0;
        }
        return (actual / recommended) * 100;
    }

    private Double round(Double value) {
        if (value == null) {
            return null;
        }
        return Math.round(value * 100.0) / 100.0;
    }
}