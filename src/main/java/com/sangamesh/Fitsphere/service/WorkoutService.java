package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.workout.*;
import com.sangamesh.Fitsphere.dto.workout.analytics.ExerciseProgressDto;
import com.sangamesh.Fitsphere.dto.workout.analytics.TrainingVolumeAnalyticsDto;
import com.sangamesh.Fitsphere.enums.WorkoutAnalyticsPeriod;

import java.util.List;

public interface WorkoutService {

    WorkoutSessionResponseDto startWorkout(StartWorkoutRequestDto request);

    WorkoutExerciseResponseDto addExercise(Long workoutId, AddWorkoutExerciseRequestDto request);

    WorkoutSetResponseDto addSet(Long workoutId, Long workoutExerciseId, AddWorkoutSetRequestDto request);

    WorkoutSessionResponseDto completeWorkout(Long workoutId);

    void reorderExercises(Long workoutId, List<Long> exerciseIds);

    List<WorkoutSessionResponseDto> getWorkoutHistory();

    WorkoutSessionResponseDto getWorkoutById(Long workoutId);

    WorkoutSessionResponseDto getActiveWorkout();

    WorkoutSetResponseDto updateSet(Long workoutId, Long workoutExerciseId, Long setId, UpdateWorkoutSetRequestDto request);

    void deleteSet(Long workoutId, Long workoutExerciseId, Long setId);

    void removeExercise(Long workoutId, Long workoutExerciseId);

    WorkoutSessionResponseDto renameWorkout(Long workoutId, RenameWorkoutRequestDto request);

    ExerciseProgressDto getExerciseProgress(Long exerciseId);

    TrainingVolumeAnalyticsDto getTrainingVolumeAnalytics(WorkoutAnalyticsPeriod period);

    void deleteWorkout(Long workoutId);

}