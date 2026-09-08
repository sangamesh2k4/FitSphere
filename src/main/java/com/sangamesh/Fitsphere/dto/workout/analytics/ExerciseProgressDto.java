package com.sangamesh.Fitsphere.dto.workout.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExerciseProgressDto {

    private Long exerciseId;

    private String exerciseName;

    private Double currentBestEstimatedOneRepMax;

    private Long totalPersonalRecords;

    private List<ExerciseProgressPointDto> history;
}
