package com.sangamesh.Fitsphere.dto.workout;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WorkoutSessionResponseDto {

    private Long id;

    private String name;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Boolean completed;

    private Double totalVolume;

    private List<WorkoutExerciseResponseDto> exercises;
}