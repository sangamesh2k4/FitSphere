package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.account.ChangeUsernameRequestDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseCategoryModulesDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseMetadataDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.ExerciseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExerciseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseService exerciseService;

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
    void getAllExercises_success() throws Exception {

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .id(1L)
                        .name("Bench Press")
                        .build();

        when(exerciseService.getAllExercises(0))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Bench Press"));

        verify(exerciseService)
                .getAllExercises(0);
    }

    @Test
    void getAllExercises_withPage_success() throws Exception {

        when(exerciseService.getAllExercises(2))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/exercises")
                        .param("page", "2"))
                .andExpect(status().isOk());

        verify(exerciseService)
                .getAllExercises(2);
    }

    @Test
    void getAllExercises_invalidPage_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/exercises")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }

    @Test
    void getExerciseById_success() throws Exception {

        ExerciseDetailDto dto =
                new ExerciseDetailDto();

        when(exerciseService.getExerciseById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isOk());

        verify(exerciseService)
                .getExerciseById(1L);
    }

    @Test
    void getExerciseById_invalidId_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/exercises/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }

    @Test
    void searchExercises_success() throws Exception {

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .id(1L)
                        .name("Bench Press")
                        .build();

        when(exerciseService.searchExercises("bench"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/exercises/search")
                        .param("keyword", "bench"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Bench Press"));

        verify(exerciseService)
                .searchExercises("bench");
    }

    @Test
    void searchExercises_missingKeyword_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/exercises/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }

    @Test
    void filterExercises_success() throws Exception {

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .id(1L)
                        .name("Bench Press")
                        .build();

        when(exerciseService.filterExercises(
                eq("bench"),
                eq(Category.CHEST),
                eq(Muscle.MIDDLE_CHEST),
                eq(Equipment.BARBELL),
                eq(Difficulty.INTERMEDIATE),
                eq(ExerciseType.COMPOUND),
                eq("name"),
                eq("asc"),
                eq(0)
        )).thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/exercises/filter")
                        .param("keyword", "bench")
                        .param("category", "CHEST")
                        .param("primaryMuscle", "MIDDLE_CHEST")
                        .param("equipment", "BARBELL")
                        .param("difficulty", "INTERMEDIATE")
                        .param("exerciseType", "COMPOUND")
                        .param("sortBy", "name")
                        .param("direction", "asc")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(exerciseService).filterExercises(
                eq("bench"),
                eq(Category.CHEST),
                eq(Muscle.MIDDLE_CHEST),
                eq(Equipment.BARBELL),
                eq(Difficulty.INTERMEDIATE),
                eq(ExerciseType.COMPOUND),
                eq("name"),
                eq("asc"),
                eq(0)
        );
    }

    @Test
    void filterExercises_defaults_success() throws Exception {

        when(exerciseService.filterExercises(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq("name"),
                eq("asc"),
                eq(0)
        )).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/exercises/filter"))
                .andExpect(status().isOk());

        verify(exerciseService).filterExercises(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq("name"),
                eq("asc"),
                eq(0)
        );
    }

    @Test
    void filterExercises_invalidEnum_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/exercises/filter")
                        .param("category", "INVALID"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }

    @Test
    void filterExercises_invalidPage_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/exercises/filter")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }

    @Test
    void getMetadata_success() throws Exception {

        ExerciseMetadataDto dto =
                ExerciseMetadataDto.builder()
                        .muscles(List.of(Muscle.BICEPS))
                        .equipment(List.of(Equipment.DUMBBELL))
                        .difficulties(List.of(Difficulty.BEGINNER))
                        .categories(List.of(Category.BICEPS))
                        .exerciseTypes(List.of(ExerciseType.ISOLATION))
                        .build();

        when(exerciseService.getExerciseMetadata())
                .thenReturn(dto);

        mockMvc.perform(get("/api/exercises/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.muscles[0]")
                        .value("BICEPS"))
                .andExpect(jsonPath("$.equipment[0]")
                        .value("DUMBBELL"))
                .andExpect(jsonPath("$.difficulties[0]")
                        .value("BEGINNER"))
                .andExpect(jsonPath("$.categories[0]")
                        .value("BICEPS"))
                .andExpect(jsonPath("$.exerciseTypes[0]")
                        .value("ISOLATION"));

        verify(exerciseService)
                .getExerciseMetadata();
    }

    @Test
    void getPrimaryMusclesByCategory_success()
            throws Exception {

        ExerciseCategoryModulesDto dto =
                ExerciseCategoryModulesDto.builder()
                        .category(Category.CHEST)
                        .muscles(List.of(
                                Muscle.UPPER_CHEST,
                                Muscle.MIDDLE_CHEST,
                                Muscle.LOWER_CHEST
                        ))
                        .build();

        when(exerciseService.getPrimaryMusclesByCategory(
                Category.CHEST))
                .thenReturn(dto);

        mockMvc.perform(get(
                        "/api/exercises/category/CHEST/primary-muscles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category")
                        .value("CHEST"))
                .andExpect(jsonPath("$.muscles[0]")
                        .value("UPPER_CHEST"))
                .andExpect(jsonPath("$.muscles[1]")
                        .value("MIDDLE_CHEST"))
                .andExpect(jsonPath("$.muscles[2]")
                        .value("LOWER_CHEST"));

        verify(exerciseService)
                .getPrimaryMusclesByCategory(Category.CHEST);
    }

    @Test
    void getPrimaryMusclesByCategory_invalidCategory_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get(
                        "/api/exercises/category/INVALID/primary-muscles"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(exerciseService);
    }
}