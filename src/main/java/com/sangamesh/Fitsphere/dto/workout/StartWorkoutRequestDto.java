package com.sangamesh.Fitsphere.dto.workout;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StartWorkoutRequestDto {

    private String name;

    private LocalDateTime startedAt;
}
