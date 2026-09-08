package com.sangamesh.Fitsphere.mapper;

import com.sangamesh.Fitsphere.dto.workout.WorkoutExerciseResponseDto;
import com.sangamesh.Fitsphere.dto.workout.WorkoutSessionResponseDto;
import com.sangamesh.Fitsphere.dto.workout.WorkoutSetResponseDto;
import com.sangamesh.Fitsphere.entity.WorkoutExercise;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import com.sangamesh.Fitsphere.entity.WorkoutSet;
import org.springframework.stereotype.Component;

@Component
public class WorkoutMapper {

    public WorkoutSetResponseDto toSetResponseDto(WorkoutSet workoutSet) {

        return WorkoutSetResponseDto.builder()
                .id(workoutSet.getId())
                .setNumber(workoutSet.getSetNumber())
                .weight(workoutSet.getWeight())
                .reps(workoutSet.getReps())
                .volume(workoutSet.getVolume())
                .estimatedOneRepMax(
                        workoutSet.getEstimatedOneRepMax())
                .personalRecord(
                        workoutSet.getPersonalRecord())
                .build();
    }

    public WorkoutExerciseResponseDto toExerciseResponseDto(WorkoutExercise workoutExercise) {

        return WorkoutExerciseResponseDto.builder()
                .id(workoutExercise.getId())
                .exerciseId(
                        workoutExercise.getExercise().getId())
                .exerciseName(
                        workoutExercise.getExercise().getName())
                .exerciseOrder(
                        workoutExercise.getExerciseOrder())
                .totalVolume(
                        workoutExercise.getTotalVolume())
                .sets(
                        workoutExercise.getSets()
                                .stream()
                                .map(this::toSetResponseDto)
                                .toList()
                )
                .build();
    }

    public WorkoutSessionResponseDto toSessionResponseDto(WorkoutSession workoutSession) {

        return WorkoutSessionResponseDto.builder()
                .id(workoutSession.getId())
                .name(workoutSession.getName())
                .startedAt(workoutSession.getStartedAt())
                .completedAt(workoutSession.getCompletedAt())
                .completed(workoutSession.getCompleted())
                .totalVolume(workoutSession.getTotalVolume())
                .exercises(
                        workoutSession.getWorkoutExercises()
                                .stream()
                                .map(this::toExerciseResponseDto)
                                .toList()
                )
                .build();
    }
}