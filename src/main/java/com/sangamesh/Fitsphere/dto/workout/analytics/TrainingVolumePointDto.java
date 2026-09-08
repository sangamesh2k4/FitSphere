package com.sangamesh.Fitsphere.dto.workout.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TrainingVolumePointDto {

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private Double totalVolume;
}