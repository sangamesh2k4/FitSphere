package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.foodlog.*;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationRequestDto;
import com.sangamesh.Fitsphere.dto.nutrition.MacroCalculationResponseDto;
import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.FoodLogMapper;
import com.sangamesh.Fitsphere.repository.FoodLogRepository;
import com.sangamesh.Fitsphere.repository.UserProfileRepository;
import com.sangamesh.Fitsphere.service.NutritionService;
import com.sangamesh.Fitsphere.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodLogServiceImplTest {

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private FoodLogMapper foodLogMapper;

    @Mock
    private NutritionService nutritionService;

    @Mock
    private UserService userService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private FoodLogServiceImpl foodLogService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
    }

    private FoodLogRequestDto createRequest() {
        FoodLogRequestDto request = new FoodLogRequestDto();
        request.setFdcId(123L);
        request.setQuantity(100.0);
        request.setUnit("g");
        return request;
    }

    private MacroCalculationResponseDto createMacros() {
        MacroCalculationResponseDto macros = new MacroCalculationResponseDto();
        macros.setFdcId(123L);
        macros.setFoodName("Chicken Breast");
        macros.setCalories(165.0);
        macros.setProtein(31.0);
        macros.setCarbohydrates(0.0);
        macros.setFat(3.6);
        macros.setFiber(0.0);
        macros.setSugar(0.0);
        macros.setSodium(74.0);
        return macros;
    }

    private FoodLog createFoodLog(
            Long id,
            LocalDate date,
            double calories,
            double protein,
            double carbs,
            double fat) {

        FoodLog foodLog = new FoodLog();
        foodLog.setId(id);
        foodLog.setUser(user);
        foodLog.setFoodName("Chicken Breast");
        foodLog.setFdcId(123L);
        foodLog.setQuantity(100.0);
        foodLog.setUnit("g");
        foodLog.setCalories(calories);
        foodLog.setProtein(protein);
        foodLog.setCarbohydrates(carbs);
        foodLog.setFat(fat);
        foodLog.setLogDate(date);
        foodLog.setLoggedAt(LocalDateTime.now());

        return foodLog;
    }

    // ---------------------------------------------------------
    // logFood
    // ---------------------------------------------------------

    @Test
    void logFood_calculatesMacrosAndSavesFoodLog() {

        FoodLogRequestDto request = createRequest();
        MacroCalculationResponseDto macros = createMacros();

        FoodLogResponseDto response = new FoodLogResponseDto();

        when(userService.getCurrentUser()).thenReturn(user);
        when(nutritionService.calculateMacros(any(MacroCalculationRequestDto.class)))
                .thenReturn(macros);
        when(foodLogRepository.save(any(FoodLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(foodLogMapper.toResponse(any(FoodLog.class)))
                .thenReturn(response);

        FoodLogResponseDto result = foodLogService.logFood(request);

        assertThat(result).isSameAs(response);

        ArgumentCaptor<MacroCalculationRequestDto> macroCaptor =
                ArgumentCaptor.forClass(MacroCalculationRequestDto.class);

        verify(nutritionService).calculateMacros(macroCaptor.capture());

        MacroCalculationRequestDto macroRequest = macroCaptor.getValue();

        assertThat(macroRequest.getFdcId()).isEqualTo(123L);
        assertThat(macroRequest.getQuantity()).isEqualTo(100.0);
        assertThat(macroRequest.getUnit()).isEqualTo("g");

        ArgumentCaptor<FoodLog> foodLogCaptor =
                ArgumentCaptor.forClass(FoodLog.class);

        verify(foodLogRepository).save(foodLogCaptor.capture());

        FoodLog saved = foodLogCaptor.getValue();

        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getFdcId()).isEqualTo(123L);
        assertThat(saved.getFoodName()).isEqualTo("Chicken Breast");
        assertThat(saved.getQuantity()).isEqualTo(100.0);
        assertThat(saved.getUnit()).isEqualTo("g");
        assertThat(saved.getCalories()).isEqualTo(165.0);
        assertThat(saved.getProtein()).isEqualTo(31.0);
        assertThat(saved.getCarbohydrates()).isEqualTo(0.0);
        assertThat(saved.getFat()).isEqualTo(3.6);
        assertThat(saved.getFiber()).isEqualTo(0.0);
        assertThat(saved.getSugar()).isEqualTo(0.0);
        assertThat(saved.getSodium()).isEqualTo(74.0);
        assertThat(saved.getLogDate()).isEqualTo(LocalDate.now());
        assertThat(saved.getLoggedAt()).isNotNull();
    }

    // ---------------------------------------------------------
    // getTodayLogs
    // ---------------------------------------------------------

    @Test
    void getTodayLogs_returnsTodaysLogsForCurrentUser() {

        FoodLog log1 = createFoodLog(
                1L,
                LocalDate.now(),
                500.0,
                30.0,
                40.0,
                15.0
        );

        FoodLog log2 = createFoodLog(
                2L,
                LocalDate.now(),
                300.0,
                20.0,
                30.0,
                10.0
        );

        FoodLogResponseDto response1 = new FoodLogResponseDto();
        FoodLogResponseDto response2 = new FoodLogResponseDto();

        when(userService.getCurrentUser()).thenReturn(user);
        when(foodLogRepository.findByUserAndLogDate(user, LocalDate.now()))
                .thenReturn(List.of(log1, log2));

        when(foodLogMapper.toResponse(log1)).thenReturn(response1);
        when(foodLogMapper.toResponse(log2)).thenReturn(response2);

        List<FoodLogResponseDto> result =
                foodLogService.getTodayLogs();

        assertThat(result)
                .containsExactly(response1, response2);

        verify(foodLogRepository)
                .findByUserAndLogDate(user, LocalDate.now());
    }

    // ---------------------------------------------------------
    // getTodaySummary
    // ---------------------------------------------------------

    @Test
    void getTodaySummary_calculatesConsumedNutritionAndRemainingCalories() {

        UserProfile profile = new UserProfile();

        profile.setRecommendedCalories(2000.0);
        profile.setRecommendedProtein(150.0);
        profile.setRecommendedCarbohydrates(250.0);
        profile.setRecommendedFat(70.0);

        FoodLog breakfast = createFoodLog(
                1L,
                LocalDate.now(),
                500.0,
                30.0,
                50.0,
                15.0
        );

        FoodLog lunch = createFoodLog(
                2L,
                LocalDate.now(),
                700.0,
                40.0,
                70.0,
                20.0
        );

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(foodLogRepository.findByUserAndLogDate(user, LocalDate.now()))
                .thenReturn(List.of(breakfast, lunch));

        DailyNutritionSummaryDto result =
                foodLogService.getTodaySummary();

        assertThat(result.getTargetCalories())
                .isEqualTo(2000.0);

        assertThat(result.getConsumedCalories())
                .isEqualTo(1200.0);

        assertThat(result.getRemainingCalories())
                .isEqualTo(800.0);

        assertThat(result.getTargetProtein())
                .isEqualTo(150.0);

        assertThat(result.getConsumedProtein())
                .isEqualTo(70.0);

        assertThat(result.getTargetCarbohydrates())
                .isEqualTo(250.0);

        assertThat(result.getConsumedCarbohydrates())
                .isEqualTo(120.0);

        assertThat(result.getTargetFat())
                .isEqualTo(70.0);

        assertThat(result.getConsumedFat())
                .isEqualTo(35.0);

        assertThat(result.getTotalMeals())
                .isEqualTo(2);
    }

    @Test
    void getTodaySummary_throwsWhenProfileDoesNotExist() {

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                foodLogService.getTodaySummary()
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");

        verify(foodLogRepository, never())
                .findByUserAndLogDate(any(), any());
    }

    // ---------------------------------------------------------
    // deleteFoodLog
    // ---------------------------------------------------------

    @Test
    void deleteFoodLog_deletesUsersFoodLog() {

        FoodLog foodLog =
                createFoodLog(
                        1L,
                        LocalDate.now(),
                        500.0,
                        30.0,
                        40.0,
                        15.0
                );

        when(userService.getCurrentUser()).thenReturn(user);
        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.of(foodLog));

        foodLogService.deleteFoodLog(1L);

        verify(foodLogRepository).delete(foodLog);
    }

    @Test
    void deleteFoodLog_throwsWhenFoodLogDoesNotExist() {

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                foodLogService.deleteFoodLog(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Food log not found");

        verify(foodLogRepository, never()).delete(any());
    }

    @Test
    void deleteFoodLog_throwsWhenFoodLogBelongsToAnotherUser() {

        User anotherUser = new User();
        anotherUser.setId(2L);

        FoodLog foodLog =
                createFoodLog(
                        1L,
                        LocalDate.now(),
                        500.0,
                        30.0,
                        40.0,
                        15.0
                );

        foodLog.setUser(anotherUser);

        when(userService.getCurrentUser()).thenReturn(user);
        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.of(foodLog));

        assertThatThrownBy(() ->
                foodLogService.deleteFoodLog(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Unauthorized");

        verify(foodLogRepository, never()).delete(any());
    }

    // ---------------------------------------------------------
    // getNutritionHistory
    // ---------------------------------------------------------

    @Test
    void getNutritionHistory_groupsLogsByDateAndCalculatesTotals() {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        FoodLog todayLog1 =
                createFoodLog(1L, today, 500.0, 30.0, 50.0, 15.0);

        FoodLog todayLog2 =
                createFoodLog(2L, today, 300.0, 20.0, 30.0, 10.0);

        FoodLog yesterdayLog =
                createFoodLog(3L, yesterday, 700.0, 40.0, 70.0, 20.0);

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        user,
                        yesterday,
                        today
                ))
                .thenReturn(List.of(
                        todayLog1,
                        todayLog2,
                        yesterdayLog
                ));

        Page<DailyNutritionHistoryDto> result =
                foodLogService.getNutritionHistory(
                        yesterday,
                        today,
                        0
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        DailyNutritionHistoryDto first =
                result.getContent().get(0);

        assertThat(first.getDate()).isEqualTo(today);
        assertThat(first.getTotalCalories()).isEqualTo(800.0);
        assertThat(first.getTotalProtein()).isEqualTo(50.0);
        assertThat(first.getTotalCarbohydrates()).isEqualTo(80.0);
        assertThat(first.getTotalFat()).isEqualTo(25.0);
        assertThat(first.getMealCount()).isEqualTo(2);

        DailyNutritionHistoryDto second =
                result.getContent().get(1);

        assertThat(second.getDate()).isEqualTo(yesterday);
        assertThat(second.getTotalCalories()).isEqualTo(700.0);
        assertThat(second.getTotalProtein()).isEqualTo(40.0);
        assertThat(second.getTotalCarbohydrates()).isEqualTo(70.0);
        assertThat(second.getTotalFat()).isEqualTo(20.0);
        assertThat(second.getMealCount()).isEqualTo(1);
    }

    @Test
    void getNutritionHistory_returnsEmptyPageWhenPageIsOutOfRange() {

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateDesc(
                        eq(user),
                        any(LocalDate.class),
                        any(LocalDate.class)
                ))
                .thenReturn(List.of());

        Page<DailyNutritionHistoryDto> result =
                foodLogService.getNutritionHistory(
                        LocalDate.now().minusDays(10),
                        LocalDate.now(),
                        1
                );

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(12);
    }

    // ---------------------------------------------------------
    // getLogsByDate
    // ---------------------------------------------------------

    @Test
    void getLogsByDate_returnsLogsForRequestedDate() {

        LocalDate date = LocalDate.of(2026, 9, 1);

        FoodLog log =
                createFoodLog(
                        1L,
                        date,
                        500.0,
                        30.0,
                        40.0,
                        15.0
                );

        FoodLogResponseDto response =
                new FoodLogResponseDto();

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository.findByUserAndLogDate(user, date))
                .thenReturn(List.of(log));

        when(foodLogMapper.toResponse(log))
                .thenReturn(response);

        List<FoodLogResponseDto> result =
                foodLogService.getLogsByDate(date);

        assertThat(result)
                .containsExactly(response);

        verify(foodLogRepository)
                .findByUserAndLogDate(user, date);
    }

    // ---------------------------------------------------------
    // updateFoodLog
    // ---------------------------------------------------------

    @Test
    void updateFoodLog_updatesNutritionValuesAndSaves() {

        FoodLog foodLog =
                createFoodLog(
                        1L,
                        LocalDate.now(),
                        500.0,
                        30.0,
                        40.0,
                        15.0
                );

        FoodLogRequestDto request = createRequest();

        MacroCalculationResponseDto macros =
                createMacros();

        macros.setFoodName("Updated Chicken");
        macros.setCalories(200.0);
        macros.setProtein(40.0);
        macros.setCarbohydrates(5.0);
        macros.setFat(4.0);
        macros.setFiber(1.0);
        macros.setSugar(2.0);
        macros.setSodium(100.0);

        FoodLogResponseDto response =
                new FoodLogResponseDto();

        when(userService.getCurrentUser()).thenReturn(user);
        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.of(foodLog));

        when(nutritionService.calculateMacros(
                any(MacroCalculationRequestDto.class)))
                .thenReturn(macros);

        when(foodLogRepository.save(foodLog))
                .thenReturn(foodLog);

        when(foodLogMapper.toResponse(foodLog))
                .thenReturn(response);

        FoodLogResponseDto result =
                foodLogService.updateFoodLog(1L, request);

        assertThat(result).isSameAs(response);

        assertThat(foodLog.getFoodName())
                .isEqualTo("Updated Chicken");

        assertThat(foodLog.getCalories())
                .isEqualTo(200.0);

        assertThat(foodLog.getProtein())
                .isEqualTo(40.0);

        assertThat(foodLog.getCarbohydrates())
                .isEqualTo(5.0);

        assertThat(foodLog.getFat())
                .isEqualTo(4.0);

        assertThat(foodLog.getFiber())
                .isEqualTo(1.0);

        assertThat(foodLog.getSugar())
                .isEqualTo(2.0);

        assertThat(foodLog.getSodium())
                .isEqualTo(100.0);

        verify(foodLogRepository).save(foodLog);
    }

    @Test
    void updateFoodLog_throwsWhenFoodLogDoesNotExist() {

        FoodLogRequestDto request = createRequest();

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                foodLogService.updateFoodLog(1L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Food log not found");

        verify(nutritionService, never())
                .calculateMacros(any());

        verify(foodLogRepository, never())
                .save(any());
    }

    @Test
    void updateFoodLog_throwsWhenFoodLogBelongsToAnotherUser() {

        User anotherUser = new User();
        anotherUser.setId(2L);

        FoodLog foodLog =
                createFoodLog(
                        1L,
                        LocalDate.now(),
                        500.0,
                        30.0,
                        40.0,
                        15.0
                );

        foodLog.setUser(anotherUser);

        FoodLogRequestDto request = createRequest();

        when(userService.getCurrentUser()).thenReturn(user);

        when(foodLogRepository.findById(1L))
                .thenReturn(Optional.of(foodLog));

        assertThatThrownBy(() ->
                foodLogService.updateFoodLog(1L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Unauthorized");

        verify(nutritionService, never())
                .calculateMacros(any());

        verify(foodLogRepository, never())
                .save(any());
    }
}