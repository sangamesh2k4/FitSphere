package com.sangamesh.Fitsphere.dto.workout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutSetResponseDto {

    private Long id;

    private Integer setNumber;

    private Double weight;

    private Integer reps;

    private Double volume;

    private Double estimatedOneRepMax;

    private Boolean personalRecord;
}