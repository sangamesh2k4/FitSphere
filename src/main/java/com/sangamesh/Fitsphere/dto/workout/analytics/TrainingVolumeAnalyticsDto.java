package com.sangamesh.Fitsphere.dto.workout.analytics;

import com.sangamesh.Fitsphere.enums.WorkoutAnalyticsPeriod;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainingVolumeAnalyticsDto {

    private WorkoutAnalyticsPeriod period;

    private Double currentPeriodVolume;

    private Double previousPeriodVolume;

    private Double volumeChangePercentage;

    private List<TrainingVolumePointDto> history;
}