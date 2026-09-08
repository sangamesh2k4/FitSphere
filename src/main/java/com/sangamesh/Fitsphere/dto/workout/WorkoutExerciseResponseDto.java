package com.sangamesh.Fitsphere.dto.workout;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutExerciseResponseDto {

    private Long id;

    private Long exerciseId;

    private String exerciseName;

    private Integer exerciseOrder;

    private Double totalVolume;

    private List<WorkoutSetResponseDto> sets;
}