package com.sangamesh.Fitsphere.dto.workout.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExerciseProgressPointDto {

    private Long workoutId;

    private String workoutName;

    private LocalDateTime performedAt;

    private Double totalVolume;

    private Double bestEstimatedOneRepMax;

    private Boolean personalRecord;
}