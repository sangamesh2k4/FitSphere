package com.sangamesh.Fitsphere.mapper;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.entity.Exercise;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

    public ExerciseSummaryDto toSummaryDto(Exercise exercise) {

        return ExerciseSummaryDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .category(exercise.getCategory())
                .primaryMuscle(exercise.getPrimaryMuscle())
                .equipment(exercise.getEquipment())
                .difficulty(exercise.getDifficulty())
                .imageUrl(exercise.getImageUrl())
                .active(exercise.getActive())
                .build();
    }

    public ExerciseDetailDto toDetailDto(Exercise exercise) {

        return ExerciseDetailDto.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .category(exercise.getCategory())
                .primaryMuscle(exercise.getPrimaryMuscle())
                .secondaryMuscles(exercise.getSecondaryMuscles())
                .movementPattern(exercise.getMovementPattern())
                .equipment(exercise.getEquipment())
                .exerciseType(exercise.getExerciseType())
                .difficulty(exercise.getDifficulty())
                .description(exercise.getDescription())

                .instructions(
                        exercise.getInstructions()
                                .stream()
                                .map(instruction -> instruction.getInstruction())
                                .collect(Collectors.toList())
                )

                .tips(
                        exercise.getTips()
                                .stream()
                                .map(tip -> tip.getTip())
                                .collect(Collectors.toList())
                )

                .commonMistakes(
                        exercise.getCommonMistakes()
                                .stream()
                                .map(mistake -> mistake.getMistake())
                                .collect(Collectors.toList())
                )
                .active(exercise.getActive())
                .imageUrl(exercise.getImageUrl())
                .build();
    }
}