package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.foodlog.DailyNutritionHistoryDto;
import com.sangamesh.Fitsphere.dto.foodlog.DailyNutritionSummaryDto;
import com.sangamesh.Fitsphere.dto.foodlog.FoodLogRequestDto;
import com.sangamesh.Fitsphere.dto.foodlog.FoodLogResponseDto;
import com.sangamesh.Fitsphere.enums.MealType;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.CurrentUserService;
import com.sangamesh.Fitsphere.service.FoodLogService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FoodLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class FoodLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimitService rateLimitService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FoodLogService foodLogService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private  JwtService jwtService;


    @Test
    void logFood_returnsCreatedFoodLog() throws Exception {

        FoodLogRequestDto request = createRequest();

        FoodLogResponseDto response = new FoodLogResponseDto();

        when(foodLogService.logFood(any(FoodLogRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/foodlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(foodLogService).logFood(any(FoodLogRequestDto.class));
    }


    @Test
    void getTodayLogs_returnsLogs() throws Exception {

        when(foodLogService.getTodayLogs())
                .thenReturn(List.of(new FoodLogResponseDto()));

        mockMvc.perform(get("/api/foodlogs/today"))
                .andExpect(status().isOk());

        verify(foodLogService).getTodayLogs();
    }


    @Test
    void getTodaySummary_returnsSummary() throws Exception {

        when(foodLogService.getTodaySummary())
                .thenReturn(new DailyNutritionSummaryDto());

        mockMvc.perform(get("/api/foodlogs/today/summary"))
                .andExpect(status().isOk());

        verify(foodLogService).getTodaySummary();
    }


    @Test
    void deleteFoodLog_returnsNoContent() throws Exception {

        doNothing().when(foodLogService).deleteFoodLog(1L);

        mockMvc.perform(delete("/api/foodlogs/1"))
                .andExpect(status().isNoContent());

        verify(foodLogService).deleteFoodLog(1L);
    }


    @Test
    void history_returnsPagedHistory() throws Exception {

        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 7);

        PageImpl<DailyNutritionHistoryDto> page =
                new PageImpl<>(
                        List.of(new DailyNutritionHistoryDto()),
                        PageRequest.of(0, 12),
                        1
                );

        when(foodLogService.getNutritionHistory(
                startDate,
                endDate,
                0
        )).thenReturn(page);

        mockMvc.perform(get("/api/foodlogs/history")
                        .param("startDate", "2026-09-01")
                        .param("endDate", "2026-09-07")
                        .param("page", "0"))
                .andExpect(status().isOk());

        verify(foodLogService)
                .getNutritionHistory(startDate, endDate, 0);
    }


    @Test
    void history_usesDefaultPageZero() throws Exception {

        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 7);

        when(foodLogService.getNutritionHistory(
                startDate,
                endDate,
                0
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        mockMvc.perform(get("/api/foodlogs/history")
                        .param("startDate", "2026-09-01")
                        .param("endDate", "2026-09-07"))
                .andExpect(status().isOk());

        verify(foodLogService)
                .getNutritionHistory(startDate, endDate, 0);
    }


    @Test
    void history_returnsBadRequestWhenStartDateMissing() throws Exception {

        mockMvc.perform(get("/api/foodlogs/history")
                        .param("endDate", "2026-09-07"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(foodLogService);
    }


    @Test
    void history_returnsBadRequestWhenEndDateMissing() throws Exception {

        mockMvc.perform(get("/api/foodlogs/history")
                        .param("startDate", "2026-09-01"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(foodLogService);
    }


    @Test
    void history_returnsBadRequestWhenDateInvalid() throws Exception {

        mockMvc.perform(get("/api/foodlogs/history")
                        .param("startDate", "invalid-date")
                        .param("endDate", "2026-09-07"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(foodLogService);
    }


    @Test
    void getLogsByDate_returnsLogs() throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 7);

        when(foodLogService.getLogsByDate(date))
                .thenReturn(List.of(new FoodLogResponseDto()));

        mockMvc.perform(get("/api/foodlogs/date")
                        .param("date", "2026-09-07"))
                .andExpect(status().isOk());

        verify(foodLogService).getLogsByDate(date);
    }


    @Test
    void getLogsByDate_returnsBadRequestWhenDateMissing() throws Exception {

        mockMvc.perform(get("/api/foodlogs/date"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(foodLogService);
    }


    @Test
    void getLogsByDate_returnsBadRequestWhenDateInvalid() throws Exception {

        mockMvc.perform(get("/api/foodlogs/date")
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(foodLogService);
    }


    @Test
    void updateFoodLog_returnsUpdatedLog() throws Exception {

        FoodLogRequestDto request = createRequest();

        when(foodLogService.updateFoodLog(
                eq(1L),
                any(FoodLogRequestDto.class)
        )).thenReturn(new FoodLogResponseDto());

        mockMvc.perform(put("/api/foodlogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(foodLogService)
                .updateFoodLog(eq(1L), any(FoodLogRequestDto.class));
    }


    @Test
    void unknownEndpoint_returnsNotFound() throws Exception {

        mockMvc.perform(get("/api/foodlogs/unknown/path"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(foodLogService);
    }


    private FoodLogRequestDto createRequest() {

        FoodLogRequestDto request = new FoodLogRequestDto();

        request.setFdcId(100L);
        request.setQuantity(100.0);
        request.setUnit("g");
        request.setMealType(MealType.BREAKFAST);

        return request;
    }
}