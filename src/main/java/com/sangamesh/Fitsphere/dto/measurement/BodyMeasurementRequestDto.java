package com.sangamesh.Fitsphere.dto.measurement;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BodyMeasurementRequestDto {

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