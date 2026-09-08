package com.sangamesh.Fitsphere.dto.measurement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MeasurementTrendDto {

    private Double value;

    private LocalDateTime recordedAt;
}