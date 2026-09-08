package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.workout.*;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.TrainingVolumeAnalyticsDto;
import com.sangamesh.Fitsphere.enums.WorkoutAnalyticsPeriod;
import com.sangamesh.Fitsphere.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    // ---- Workouts ----

    @PostMapping
    public ResponseEntity<WorkoutSessionResponseDto> startWorkout(@RequestBody StartWorkoutRequestDto request) {
        WorkoutSessionResponseDto response = workoutService.startWorkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{workoutId}/exercises/reorder")
    public ResponseEntity<Void> reorderExercises(
            @PathVariable Long workoutId,
            @RequestBody ReorderWorkoutExercisesRequestDto request) {

        workoutService.reorderExercises(
                workoutId,
                request.getExerciseIds()
        );

        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public List<WorkoutSessionResponseDto> getWorkoutHistory() {
        return workoutService.getWorkoutHistory();
    }

    @GetMapping("/active")
    public WorkoutSessionResponseDto getActiveWorkout() {
        return workoutService.getActiveWorkout();
    }

    @GetMapping("/{workoutId}")
    public WorkoutSessionResponseDto getWorkoutById(@PathVariable Long workoutId) {
        return workoutService.getWorkoutById(workoutId);
    }

    @PostMapping("/{workoutId}/complete")
    public WorkoutSessionResponseDto completeWorkout(@PathVariable Long workoutId) {
        return workoutService.completeWorkout(workoutId);
    }
    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long workoutId) {

        workoutService.deleteWorkout(workoutId);

        return ResponseEntity.noContent().build();
    }

    // ---- Exercises (nested under workout) ----

    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutExerciseResponseDto> addExercise(
            @PathVariable Long workoutId,
            @RequestBody AddWorkoutExerciseRequestDto request) {
        WorkoutExerciseResponseDto response = workoutService.addExercise(workoutId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}")
    public ResponseEntity<Void> removeExercise(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId) {
        workoutService.removeExercise(workoutId, workoutExerciseId);
        return ResponseEntity.noContent().build();
    }

    // ---- Sets (nested under workout + exercise) ----

    @PostMapping("/{workoutId}/exercises/{workoutExerciseId}/sets")
    public ResponseEntity<WorkoutSetResponseDto> addSet(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId,
            @RequestBody AddWorkoutSetRequestDto request) {
        WorkoutSetResponseDto response = workoutService.addSet(workoutId, workoutExerciseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{workoutId}/exercises/{workoutExerciseId}/sets/{setId}")
    public WorkoutSetResponseDto updateSet(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId,
            @PathVariable Long setId,
            @RequestBody UpdateWorkoutSetRequestDto request) {
        return workoutService.updateSet(workoutId, workoutExerciseId, setId, request);
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId,
            @PathVariable Long setId) {
        workoutService.deleteSet(workoutId, workoutExerciseId, setId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{workoutId}")
    public WorkoutSessionResponseDto renameWorkout(
            @PathVariable Long workoutId,
            @RequestBody RenameWorkoutRequestDto request) {

        return workoutService.renameWorkout(workoutId, request);
    }

    // ---- Analytics ----

    @GetMapping("/progress/exercises/{exerciseId}")
    public ExerciseProgressDto getExerciseProgress(@PathVariable Long exerciseId) {
        return workoutService.getExerciseProgress(exerciseId);
    }

    @GetMapping("/analytics/volume")
    public TrainingVolumeAnalyticsDto getTrainingVolumeAnalytics(@RequestParam WorkoutAnalyticsPeriod period) {
        return workoutService.getTrainingVolumeAnalytics(period);
    }
}