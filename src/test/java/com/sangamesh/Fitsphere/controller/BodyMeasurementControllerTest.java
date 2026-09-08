package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementRequestDto;
import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementResponseDto;
import com.sangamesh.Fitsphere.dto.measurement.MeasurementTrendDto;
import com.sangamesh.Fitsphere.enums.MeasurementMetric;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.BodyMeasurementService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BodyMeasurementController.class)
@AutoConfigureMockMvc(addFilters = false)
class BodyMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BodyMeasurementService bodyMeasurementService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void addMeasurement_success() throws Exception {

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        request.setWeight(70.0);

        BodyMeasurementResponseDto response =
                BodyMeasurementResponseDto.builder()
                        .id(1L)
                        .weight(70.0)
                        .recordedAt(LocalDateTime.of(
                                2026, 1, 1, 10, 0))
                        .build();

        when(bodyMeasurementService.addMeasurement(
                any(BodyMeasurementRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/measurements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.weight").value(70.0));

        verify(bodyMeasurementService)
                .addMeasurement(any(BodyMeasurementRequestDto.class));
    }

    @Test
    void getMeasurementHistory_success() throws Exception {

        BodyMeasurementResponseDto response =
                BodyMeasurementResponseDto.builder()
                        .id(1L)
                        .weight(70.0)
                        .build();

        when(bodyMeasurementService.getMeasurementHistory(0))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/measurements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].weight").value(70.0));

        verify(bodyMeasurementService)
                .getMeasurementHistory(0);
    }

    @Test
    void getMeasurementHistory_withPage_success() throws Exception {

        when(bodyMeasurementService.getMeasurementHistory(2))
                .thenReturn(new PageImpl<>(
                        List.of()
                ));

        mockMvc.perform(get("/api/measurements")
                        .param("page", "2"))
                .andExpect(status().isOk());

        verify(bodyMeasurementService)
                .getMeasurementHistory(2);
    }

    @Test
    void getLatestMeasurement_success() throws Exception {

        BodyMeasurementResponseDto response =
                BodyMeasurementResponseDto.builder()
                        .id(1L)
                        .weight(72.0)
                        .build();

        when(bodyMeasurementService.getLatestMeasurement())
                .thenReturn(response);

        mockMvc.perform(get("/api/measurements/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.weight").value(72.0));

        verify(bodyMeasurementService)
                .getLatestMeasurement();
    }

    @Test
    void deleteMeasurement_success() throws Exception {

        doNothing().when(bodyMeasurementService)
                .deleteMeasurement(1L);

        mockMvc.perform(delete("/api/measurements/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Body measurement deleted successfully"));

        verify(bodyMeasurementService)
                .deleteMeasurement(1L);
    }

    @Test
    void updateMeasurement_success() throws Exception {

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        request.setWeight(75.0);

        BodyMeasurementResponseDto response =
                BodyMeasurementResponseDto.builder()
                        .id(1L)
                        .weight(75.0)
                        .build();

        when(bodyMeasurementService.updateMeasurement(
                eq(1L),
                any(BodyMeasurementRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/measurements/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.weight").value(75.0));

        verify(bodyMeasurementService)
                .updateMeasurement(
                        eq(1L),
                        any(BodyMeasurementRequestDto.class));
    }

    @Test
    void getMeasurementTrend_success() throws Exception {

        MeasurementTrendDto trend =
                new MeasurementTrendDto(
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                );

        when(bodyMeasurementService.getMeasurementTrend(
                MeasurementMetric.WEIGHT))
                .thenReturn(List.of(trend));

        mockMvc.perform(get("/api/measurements/trend")
                        .param("metric", "WEIGHT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(70.0));

        verify(bodyMeasurementService)
                .getMeasurementTrend(MeasurementMetric.WEIGHT);
    }

    @Test
    void getMeasurementTrend_missingMetric_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/measurements/trend"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMeasurementTrend_invalidMetric_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/measurements/trend")
                        .param("metric", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMeasurementHistory_invalidPage_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/measurements")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMeasurement_invalidId_returnsBadRequest()
            throws Exception {

        mockMvc.perform(delete("/api/measurements/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMeasurement_invalidId_returnsBadRequest()
            throws Exception {

        mockMvc.perform(patch("/api/measurements/abc")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}