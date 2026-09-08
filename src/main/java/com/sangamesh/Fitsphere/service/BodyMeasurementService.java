package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementRequestDto;
import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementResponseDto;
import com.sangamesh.Fitsphere.dto.measurement.MeasurementTrendDto;
import com.sangamesh.Fitsphere.enums.MeasurementMetric;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BodyMeasurementService {


    BodyMeasurementResponseDto addMeasurement(
            BodyMeasurementRequestDto request);

    Page<BodyMeasurementResponseDto> getMeasurementHistory(int page);

    BodyMeasurementResponseDto getLatestMeasurement();

    void deleteMeasurement(Long id);

    BodyMeasurementResponseDto updateMeasurement(Long id, BodyMeasurementRequestDto request);

    List<MeasurementTrendDto> getMeasurementTrend(MeasurementMetric metric);

}
