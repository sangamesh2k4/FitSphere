package com.sangamesh.Fitsphere.dto.measurement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BodyMeasurementResponseDto {

    private Long id;

    private Double weight;

    private Double bodyFatPercentage;

    private Double waist;

    private Double chest;

    private Double leftArm;

    private Double rightArm;

    private Double leftThigh;

    private Double rightThigh;

    private LocalDateTime recordedAt;
}
