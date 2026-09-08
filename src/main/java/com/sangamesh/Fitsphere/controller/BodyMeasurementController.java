package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementRequestDto;
import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementResponseDto;
import com.sangamesh.Fitsphere.dto.measurement.MeasurementTrendDto;
import com.sangamesh.Fitsphere.enums.MeasurementMetric;
import com.sangamesh.Fitsphere.service.BodyMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class BodyMeasurementController {

    private final BodyMeasurementService bodyMeasurementService;

    @PostMapping
    public BodyMeasurementResponseDto addMeasurement(
            @RequestBody BodyMeasurementRequestDto request) {

        return bodyMeasurementService.addMeasurement(request);
    }

    @GetMapping
    public Page<BodyMeasurementResponseDto> getMeasurementHistory(@RequestParam(defaultValue = "0")int page) {

        return bodyMeasurementService.getMeasurementHistory(page);
    }

    @GetMapping("/latest")
    public BodyMeasurementResponseDto getLatestMeasurement() {

        return bodyMeasurementService.getLatestMeasurement();
    }

    @DeleteMapping("/{id}")
    public String deleteMeasurement(@PathVariable Long id) {

        bodyMeasurementService.deleteMeasurement(id);

        return "Body measurement deleted successfully";
    }

    @PatchMapping("/{id}")
    public BodyMeasurementResponseDto updateMeasurement(
            @PathVariable Long id,
            @RequestBody BodyMeasurementRequestDto request) {

        return bodyMeasurementService.updateMeasurement(id, request);
    }

    @GetMapping("/trend")
    public List<MeasurementTrendDto> getMeasurementTrend(
            @RequestParam MeasurementMetric metric) {

        return bodyMeasurementService
                .getMeasurementTrend(metric);
    }
}