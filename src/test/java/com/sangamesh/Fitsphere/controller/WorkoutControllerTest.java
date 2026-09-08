package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.workout.*;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.TrainingVolumeAnalyticsDto;
import com.sangamesh.Fitsphere.enums.WorkoutAnalyticsPeriod;
import com.sangamesh.Fitsphere.exception.GlobalExceptionHandler;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import com.sangamesh.Fitsphere.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // ---------------------------------------------------------
    // START WORKOUT
    // ---------------------------------------------------------

    @Test
    void startWorkout_returnsCreated() throws Exception {

        StartWorkoutRequestDto request = new StartWorkoutRequestDto();
        request.setName("Push Day");

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(1L)
                        .name("Push Day")
                        .completed(false)
                        .totalVolume(0.0)
                        .build();

        when(workoutService.startWorkout(any(StartWorkoutRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/workouts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Push Day"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(workoutService).startWorkout(any(StartWorkoutRequestDto.class));
    }

    // ---------------------------------------------------------
    // REORDER
    // ---------------------------------------------------------

    @Test
    void reorderExercises_returnsNoContent() throws Exception {

        ReorderWorkoutExercisesRequestDto request =
                new ReorderWorkoutExercisesRequestDto();

        request.setExerciseIds(List.of(3L, 1L, 2L));

        doNothing().when(workoutService)
                .reorderExercises(10L, request.getExerciseIds());

        mockMvc.perform(patch("/api/workouts/10/exercises/reorder")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(workoutService)
                .reorderExercises(10L, request.getExerciseIds());
    }

    // ---------------------------------------------------------
    // WORKOUT HISTORY
    // ---------------------------------------------------------

    @Test
    void getWorkoutHistory_returnsOk() throws Exception {

        WorkoutSessionResponseDto workout =
                WorkoutSessionResponseDto.builder()
                        .id(1L)
                        .name("Push Day")
                        .completed(true)
                        .totalVolume(1500.0)
                        .build();

        when(workoutService.getWorkoutHistory())
                .thenReturn(List.of(workout));

        mockMvc.perform(get("/api/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Push Day"))
                .andExpect(jsonPath("$[0].completed").value(true));

        verify(workoutService).getWorkoutHistory();
    }

    // ---------------------------------------------------------
    // ACTIVE WORKOUT
    // ---------------------------------------------------------

    @Test
    void getActiveWorkout_returnsOk() throws Exception {

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(5L)
                        .name("Leg Day")
                        .completed(false)
                        .totalVolume(800.0)
                        .build();

        when(workoutService.getActiveWorkout())
                .thenReturn(response);

        mockMvc.perform(get("/api/workouts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Leg Day"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(workoutService).getActiveWorkout();
    }

    // ---------------------------------------------------------
    // GET WORKOUT BY ID
    // ---------------------------------------------------------

    @Test
    void getWorkoutById_returnsOk() throws Exception {

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(7L)
                        .name("Chest Day")
                        .completed(false)
                        .build();

        when(workoutService.getWorkoutById(7L))
                .thenReturn(response);

        mockMvc.perform(get("/api/workouts/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Chest Day"));

        verify(workoutService).getWorkoutById(7L);
    }

    // ---------------------------------------------------------
    // COMPLETE WORKOUT
    // ---------------------------------------------------------

    @Test
    void completeWorkout_returnsOk() throws Exception {

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(3L)
                        .name("Pull Day")
                        .completed(true)
                        .totalVolume(2000.0)
                        .build();

        when(workoutService.completeWorkout(3L))
                .thenReturn(response);

        mockMvc.perform(post("/api/workouts/3/complete")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.totalVolume").value(2000.0));

        verify(workoutService).completeWorkout(3L);
    }

    // ---------------------------------------------------------
    // DELETE WORKOUT
    // ---------------------------------------------------------

    @Test
    void deleteWorkout_returnsNoContent() throws Exception {

        doNothing().when(workoutService)
                .deleteWorkout(4L);

        mockMvc.perform(delete("/api/workouts/4").with(csrf()))
                .andExpect(status().isNoContent());

        verify(workoutService).deleteWorkout(4L);
    }

    // ---------------------------------------------------------
    // ADD EXERCISE
    // ---------------------------------------------------------

    @Test
    void addExercise_returnsCreated() throws Exception {

        AddWorkoutExerciseRequestDto request =
                new AddWorkoutExerciseRequestDto();

        request.setExerciseId(25L);

        WorkoutExerciseResponseDto response =
                WorkoutExerciseResponseDto.builder()
                        .id(100L)
                        .exerciseId(25L)
                        .exerciseName("Bench Press")
                        .exerciseOrder(1)
                        .totalVolume(0.0)
                        .build();

        when(workoutService.addExercise(eq(10L), any(AddWorkoutExerciseRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/workouts/10/exercises")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.exerciseId").value(25))
                .andExpect(jsonPath("$.exerciseName").value("Bench Press"))
                .andExpect(jsonPath("$.exerciseOrder").value(1));

        verify(workoutService)
                .addExercise(eq(10L), any(AddWorkoutExerciseRequestDto.class));
    }

    // ---------------------------------------------------------
    // REMOVE EXERCISE
    // ---------------------------------------------------------

    @Test
    void removeExercise_returnsNoContent() throws Exception {

        doNothing().when(workoutService)
                .removeExercise(10L, 100L);

        mockMvc.perform(delete("/api/workouts/10/exercises/100")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(workoutService)
                .removeExercise(10L, 100L);
    }

    // ---------------------------------------------------------
    // ADD SET
    // ---------------------------------------------------------

    @Test
    void addSet_returnsCreated() throws Exception {

        AddWorkoutSetRequestDto request =
                new AddWorkoutSetRequestDto();

        request.setWeight(80.0);
        request.setReps(10);

        WorkoutSetResponseDto response =
                WorkoutSetResponseDto.builder()
                        .id(500L)
                        .setNumber(1)
                        .weight(80.0)
                        .reps(10)
                        .volume(800.0)
                        .estimatedOneRepMax(106.67)
                        .personalRecord(true)
                        .build();

        when(workoutService.addSet(
                eq(10L),
                eq(100L),
                any(AddWorkoutSetRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/workouts/10/exercises/100/sets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(500))
                .andExpect(jsonPath("$.setNumber").value(1))
                .andExpect(jsonPath("$.weight").value(80.0))
                .andExpect(jsonPath("$.reps").value(10))
                .andExpect(jsonPath("$.volume").value(800.0))
                .andExpect(jsonPath("$.personalRecord").value(true));

        verify(workoutService).addSet(
                eq(10L),
                eq(100L),
                any(AddWorkoutSetRequestDto.class));
    }

    // ---------------------------------------------------------
    // UPDATE SET
    // ---------------------------------------------------------

    @Test
    void updateSet_returnsOk() throws Exception {

        UpdateWorkoutSetRequestDto request =
                new UpdateWorkoutSetRequestDto();

        request.setWeight(90.0);
        request.setReps(8);

        WorkoutSetResponseDto response =
                WorkoutSetResponseDto.builder()
                        .id(500L)
                        .setNumber(1)
                        .weight(90.0)
                        .reps(8)
                        .volume(720.0)
                        .estimatedOneRepMax(114.0)
                        .personalRecord(true)
                        .build();

        when(workoutService.updateSet(
                eq(10L),
                eq(100L),
                eq(500L),
                any(UpdateWorkoutSetRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/workouts/10/exercises/100/sets/500").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500))
                .andExpect(jsonPath("$.weight").value(90.0))
                .andExpect(jsonPath("$.reps").value(8))
                .andExpect(jsonPath("$.volume").value(720.0));

        verify(workoutService).updateSet(
                eq(10L),
                eq(100L),
                eq(500L),
                any(UpdateWorkoutSetRequestDto.class));
    }

    // ---------------------------------------------------------
    // DELETE SET
    // ---------------------------------------------------------

    @Test
    void deleteSet_returnsNoContent() throws Exception {

        doNothing().when(workoutService)
                .deleteSet(10L, 100L, 500L);

        mockMvc.perform(delete("/api/workouts/10/exercises/100/sets/500").with(csrf()))
                .andExpect(status().isNoContent());

        verify(workoutService)
                .deleteSet(10L, 100L, 500L);
    }

    // ---------------------------------------------------------
    // RENAME WORKOUT
    // ---------------------------------------------------------

    @Test
    void renameWorkout_returnsOk() throws Exception {

        RenameWorkoutRequestDto request =
                new RenameWorkoutRequestDto();

        request.setName("Upper Body");

        WorkoutSessionResponseDto response =
                WorkoutSessionResponseDto.builder()
                        .id(10L)
                        .name("Upper Body")
                        .completed(false)
                        .build();

        when(workoutService.renameWorkout(
                eq(10L),
                any(RenameWorkoutRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/workouts/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Upper Body"));

        verify(workoutService)
                .renameWorkout(eq(10L), any(RenameWorkoutRequestDto.class));
    }

    // ---------------------------------------------------------
    // EXERCISE PROGRESS
    // ---------------------------------------------------------

    @Test
    void getExerciseProgress_returnsOk() throws Exception {

        ExerciseProgressDto response =
                ExerciseProgressDto.builder()
                        .exerciseId(25L)
                        .exerciseName("Bench Press")
                        .currentBestEstimatedOneRepMax(110.0)
                        .totalPersonalRecords(3L)
                        .history(List.of())
                        .build();

        when(workoutService.getExerciseProgress(25L))
                .thenReturn(response);

        mockMvc.perform(get("/api/workouts/progress/exercises/25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseId").value(25))
                .andExpect(jsonPath("$.exerciseName").value("Bench Press"))
                .andExpect(jsonPath("$.currentBestEstimatedOneRepMax").value(110.0))
                .andExpect(jsonPath("$.totalPersonalRecords").value(3))
                .andExpect(jsonPath("$.history").isArray());

        verify(workoutService).getExerciseProgress(25L);
    }

    // ---------------------------------------------------------
    // TRAINING VOLUME ANALYTICS
    // ---------------------------------------------------------

    @Test
    void getTrainingVolumeAnalytics_returnsOk() throws Exception {

        TrainingVolumeAnalyticsDto response =
                TrainingVolumeAnalyticsDto.builder()
                        .period(WorkoutAnalyticsPeriod.WEEKLY)
                        .currentPeriodVolume(2000.0)
                        .previousPeriodVolume(1500.0)
                        .volumeChangePercentage(33.33)
                        .history(List.of())
                        .build();

        when(workoutService.getTrainingVolumeAnalytics(
                WorkoutAnalyticsPeriod.WEEKLY))
                .thenReturn(response);

        mockMvc.perform(get("/api/workouts/analytics/volume")
                        .param("period", "WEEKLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("WEEKLY"))
                .andExpect(jsonPath("$.currentPeriodVolume").value(2000.0))
                .andExpect(jsonPath("$.previousPeriodVolume").value(1500.0))
                .andExpect(jsonPath("$.volumeChangePercentage").value(33.33));

        verify(workoutService)
                .getTrainingVolumeAnalytics(WorkoutAnalyticsPeriod.WEEKLY);
    }

    @Test
    void getTrainingVolumeAnalytics_missingPeriod_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/workouts/analytics/volume"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workoutService);
    }

    // ---------------------------------------------------------
    // INVALID HTTP METHODS / ROUTES
    // ---------------------------------------------------------

    @Test
    void unknownWorkoutEndpoint_returnsNotFound() throws Exception {

        mockMvc.perform(get("/api/workouts/unknown/path/add"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}