package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.analytics.DashboardAnalyticsDto;
import com.sangamesh.Fitsphere.entity.BodyMeasurement;
import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import com.sangamesh.Fitsphere.repository.BodyMeasurementRepository;
import com.sangamesh.Fitsphere.repository.FoodLogRepository;
import com.sangamesh.Fitsphere.repository.UserProfileRepository;
import com.sangamesh.Fitsphere.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private BodyMeasurementRepository bodyMeasurementRepository;

    private AnalyticsServiceImpl analyticsService;

    private User user;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(
                userService,
                userProfileRepository,
                foodLogRepository,
                bodyMeasurementRepository
        );

        user = new User();
        user.setId(1L);

        when(userService.getCurrentUser())
                .thenReturn(user);
    }

    @Test
    void getDashboardAnalytics_shouldCalculateAllValues() {

        UserProfile profile = new UserProfile();
        profile.setWeight(75.0);
        profile.setBodyFatPercentage(18.0);
        profile.setRecommendedCalories(2000.0);
        profile.setRecommendedProtein(150.0);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        FoodLog log1 = new FoodLog();
        log1.setLogDate(today);
        log1.setCalories(1800.0);
        log1.setProtein(120.0);

        FoodLog log2 = new FoodLog();
        log2.setLogDate(yesterday);
        log2.setCalories(2200.0);
        log2.setProtein(180.0);

        BodyMeasurement current = new BodyMeasurement();
        current.setWeight(74.5);
        current.setBodyFatPercentage(17.5);

        BodyMeasurement previous = new BodyMeasurement();
        previous.setWeight(75.5);
        previous.setBodyFatPercentage(18.0);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(log1, log2));

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of(current, previous));

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(74.5, result.getCurrentWeight());
        assertEquals(-1.0, result.getWeightChange());

        assertEquals(17.5, result.getCurrentBodyFatPercentage());
        assertEquals(-0.5, result.getBodyFatChange());

        assertEquals(2000.0, result.getAverageDailyCalories());
        assertEquals(2000.0, result.getRecommendedCalories());

        assertEquals(150.0, result.getAverageDailyProtein());
        assertEquals(150.0, result.getRecommendedProtein());

        assertEquals(100.0, result.getCalorieTargetPercentage());
        assertEquals(100.0, result.getProteinTargetPercentage());

        assertEquals(2, result.getDaysLogged());
    }

    @Test
    void getDashboardAnalytics_shouldUseProfileWhenNoMeasurements() {

        UserProfile profile = new UserProfile();
        profile.setWeight(80.0);
        profile.setBodyFatPercentage(20.0);
        profile.setRecommendedCalories(2500.0);
        profile.setRecommendedProtein(160.0);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of());

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of());

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(80.0, result.getCurrentWeight());
        assertEquals(20.0, result.getCurrentBodyFatPercentage());

        assertEquals(0.0, result.getWeightChange());
        assertEquals(0.0, result.getBodyFatChange());

        assertEquals(0.0, result.getAverageDailyCalories());
        assertEquals(0.0, result.getAverageDailyProtein());

        assertEquals(2500.0, result.getRecommendedCalories());
        assertEquals(160.0, result.getRecommendedProtein());

        assertEquals(0.0, result.getCalorieTargetPercentage());
        assertEquals(0.0, result.getProteinTargetPercentage());

        assertEquals(0, result.getDaysLogged());
    }

    @Test
    void getDashboardAnalytics_whenProfileDoesNotExist_shouldReturnNullProfileValues() {

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of());

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of());

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertNull(result.getCurrentWeight());
        assertNull(result.getCurrentBodyFatPercentage());

        assertNull(result.getRecommendedCalories());
        assertNull(result.getRecommendedProtein());

        assertEquals(0.0, result.getWeightChange());
        assertEquals(0.0, result.getBodyFatChange());

        assertEquals(0.0, result.getCalorieTargetPercentage());
        assertEquals(0.0, result.getProteinTargetPercentage());

        assertEquals(0, result.getDaysLogged());
    }

    @Test
    void getDashboardAnalytics_shouldIgnoreNullFoodLogValuesAndDuplicateDates() {

        UserProfile profile = new UserProfile();
        profile.setRecommendedCalories(2000.0);
        profile.setRecommendedProtein(100.0);

        LocalDate today = LocalDate.now();

        FoodLog log1 = new FoodLog();
        log1.setLogDate(today);
        log1.setCalories(1000.0);
        log1.setProtein(50.0);

        FoodLog log2 = new FoodLog();
        log2.setLogDate(today);
        log2.setCalories(null);
        log2.setProtein(null);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of(log1, log2));

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of());

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(1, result.getDaysLogged());

        assertEquals(1000.0, result.getAverageDailyCalories());
        assertEquals(50.0, result.getAverageDailyProtein());

        assertEquals(50.0, result.getCalorieTargetPercentage());
        assertEquals(50.0, result.getProteinTargetPercentage());
    }

    @Test
    void getDashboardAnalytics_shouldRoundValuesToTwoDecimals() {

        UserProfile profile = new UserProfile();
        profile.setRecommendedCalories(2000.0);
        profile.setRecommendedProtein(100.0);

        FoodLog log1 = new FoodLog();
        log1.setLogDate(LocalDate.now());
        log1.setCalories(1001.0);
        log1.setProtein(33.333);

        BodyMeasurement current = new BodyMeasurement();
        current.setWeight(74.567);
        current.setBodyFatPercentage(17.456);

        BodyMeasurement previous = new BodyMeasurement();
        previous.setWeight(75.123);
        previous.setBodyFatPercentage(18.789);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of(log1));

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of(current, previous));

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(74.57, result.getCurrentWeight());
        assertEquals(-0.56, result.getWeightChange());

        assertEquals(17.46, result.getCurrentBodyFatPercentage());
        assertEquals(-1.33, result.getBodyFatChange());

        assertEquals(1001.0, result.getAverageDailyCalories());
        assertEquals(33.33, result.getAverageDailyProtein());

        assertEquals(50.05, result.getCalorieTargetPercentage());
        assertEquals(33.33, result.getProteinTargetPercentage());
    }

    @Test
    void getDashboardAnalytics_whenOnlyOneMeasurement_shouldReturnZeroChange() {

        UserProfile profile = new UserProfile();
        profile.setRecommendedCalories(2000.0);
        profile.setRecommendedProtein(100.0);

        BodyMeasurement measurement = new BodyMeasurement();
        measurement.setWeight(75.0);
        measurement.setBodyFatPercentage(18.0);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of());

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of(measurement));

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(75.0, result.getCurrentWeight());
        assertEquals(18.0, result.getCurrentBodyFatPercentage());

        assertEquals(0.0, result.getWeightChange());
        assertEquals(0.0, result.getBodyFatChange());
    }

    @Test
    void getDashboardAnalytics_whenRecommendedValuesAreZero_shouldReturnZeroTargets() {

        UserProfile profile = new UserProfile();
        profile.setRecommendedCalories(0.0);
        profile.setRecommendedProtein(0.0);

        FoodLog log = new FoodLog();
        log.setLogDate(LocalDate.now());
        log.setCalories(2000.0);
        log.setProtein(100.0);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        any(), any(), any()))
                .thenReturn(List.of(log));

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(1L))
                .thenReturn(List.of());

        DashboardAnalyticsDto result =
                analyticsService.getDashboardAnalytics();

        assertEquals(0.0, result.getCalorieTargetPercentage());
        assertEquals(0.0, result.getProteinTargetPercentage());
    }
}