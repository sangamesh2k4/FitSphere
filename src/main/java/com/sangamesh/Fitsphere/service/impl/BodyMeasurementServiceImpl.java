package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementRequestDto;
import com.sangamesh.Fitsphere.dto.measurement.BodyMeasurementResponseDto;
import com.sangamesh.Fitsphere.dto.measurement.MeasurementTrendDto;
import com.sangamesh.Fitsphere.entity.BodyMeasurement;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.MeasurementMetric;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.repository.BodyMeasurementRepository;
import com.sangamesh.Fitsphere.service.BodyMeasurementService;
import com.sangamesh.Fitsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BodyMeasurementServiceImpl
        implements BodyMeasurementService {

    private final BodyMeasurementRepository bodyMeasurementRepository;

    private final UserService userService;

    private static final int PAGE_SIZE=12;
    private static final int TREND_LIMIT = 12;


    @Override
    public BodyMeasurementResponseDto addMeasurement(BodyMeasurementRequestDto request) {
        User user = userService.getCurrentUser();
        validateMeasurement(request);

        BodyMeasurement latest = bodyMeasurementRepository
                        .findFirstByUserIdOrderByRecordedAtDesc(user.getId())
                        .orElse(null);
        BodyMeasurement measurement = new BodyMeasurement();
        measurement.setUser(user);

        measurement.setWeight(request.getWeight() != null ? request.getWeight()
                        : latest != null ? latest.getWeight() : null);

        measurement.setBodyFatPercentage(request.getBodyFatPercentage() != null ? request.getBodyFatPercentage()
                        : latest != null ? latest.getBodyFatPercentage() : null);

        measurement.setWaist(request.getWaist() != null ? request.getWaist()
                        : latest != null ? latest.getWaist() : null);

        measurement.setChest(request.getChest() != null ? request.getChest()
                        : latest != null ? latest.getChest() : null);

        measurement.setLeftArm(request.getLeftArm() != null ? request.getLeftArm()
                        : latest != null ? latest.getLeftArm() : null);

        measurement.setRightArm(request.getRightArm() != null ? request.getRightArm()
                        : latest != null ? latest.getRightArm() : null);

        measurement.setLeftThigh(request.getLeftThigh() != null ? request.getLeftThigh()
                        : latest != null ? latest.getLeftThigh() : null);

        measurement.setRightThigh(request.getRightThigh() != null ? request.getRightThigh()
                        : latest != null ? latest.getRightThigh() : null);

        measurement.setRecordedAt(request.getRecordedAt() != null
                ? request.getRecordedAt() : LocalDateTime.now());

        BodyMeasurement savedMeasurement = bodyMeasurementRepository.save(measurement);
        return toResponseDto(savedMeasurement);
    }

    @Override
    public Page<BodyMeasurementResponseDto> getMeasurementHistory(int page) {
        User user = userService.getCurrentUser();
        Pageable pageable= PageRequest.of(page,PAGE_SIZE);
        return bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(user.getId(),pageable)
                .map(this::toResponseDto);

    }

    @Override
    public BodyMeasurementResponseDto getLatestMeasurement() {
        User user = userService.getCurrentUser();
        BodyMeasurement measurement = bodyMeasurementRepository
                        .findFirstByUserIdOrderByRecordedAtDescIdDesc(
                                user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("No body measurements found"));
        return toResponseDto(measurement);
    }

    @Override
    public void deleteMeasurement(Long id) {

        User user = userService.getCurrentUser();

        BodyMeasurement measurement =
                bodyMeasurementRepository
                        .findByIdAndUserId(id, user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Body measurement not found"));

        bodyMeasurementRepository.delete(measurement);
    }

    private BodyMeasurementResponseDto toResponseDto(
            BodyMeasurement measurement) {

        return BodyMeasurementResponseDto.builder()
                .id(measurement.getId())
                .weight(measurement.getWeight())
                .bodyFatPercentage(
                        measurement.getBodyFatPercentage())
                .waist(measurement.getWaist())
                .chest(measurement.getChest())
                .leftArm(measurement.getLeftArm())
                .rightArm(measurement.getRightArm())
                .leftThigh(measurement.getLeftThigh())
                .rightThigh(measurement.getRightThigh())
                .recordedAt(measurement.getRecordedAt())
                .build();
    }

    private void validateMeasurement(
            BodyMeasurementRequestDto request) {

        if (request.getWeight() == null
                && request.getBodyFatPercentage() == null
                && request.getWaist() == null
                && request.getChest() == null
                && request.getLeftArm() == null
                && request.getRightArm() == null
                && request.getLeftThigh() == null
                && request.getRightThigh() == null) {

            throw new BadRequestException(
                    "At least one body measurement is required");
        }
    }
    @Override
    public BodyMeasurementResponseDto updateMeasurement(Long id, BodyMeasurementRequestDto request) {
        User user = userService.getCurrentUser();

        BodyMeasurement measurement = bodyMeasurementRepository
                        .findByIdAndUserId(id, user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Body measurement not found"));

        validateMeasurement(request);

        if (request.getWeight() != null) {
            measurement.setWeight(request.getWeight());
        }

        if (request.getBodyFatPercentage() != null) {
            measurement.setBodyFatPercentage(request.getBodyFatPercentage());
        }
        if (request.getWaist() != null) {
            measurement.setWaist(request.getWaist());
        }
        if (request.getChest() != null) {
            measurement.setChest(request.getChest());
        }
        if (request.getLeftArm() != null) {
            measurement.setLeftArm(request.getLeftArm());
        }
        if (request.getRightArm() != null) {
            measurement.setRightArm(request.getRightArm());
        }
        if (request.getLeftThigh() != null) {
            measurement.setLeftThigh(request.getLeftThigh());
        }
        if (request.getRightThigh() != null) {
            measurement.setRightThigh(request.getRightThigh());
        }
        if (request.getRecordedAt() != null) {
            measurement.setRecordedAt(request.getRecordedAt());
        }
        BodyMeasurement updatedMeasurement = bodyMeasurementRepository.save(measurement);
        return toResponseDto(updatedMeasurement);
    }

    @Override
    public List<MeasurementTrendDto> getMeasurementTrend(
            MeasurementMetric metric) {

        User user = userService.getCurrentUser();
        List<BodyMeasurement> measurements=bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtAscIdAsc(user.getId());
        if(measurements.size()>TREND_LIMIT){
            measurements=measurements.subList(measurements.size()-TREND_LIMIT, measurements.size());
        }

        return measurements.stream().map(measurement->{
            Double value=getMetricValue(measurement,metric);
            if(value==null){
                return null;
            }
            return new MeasurementTrendDto(value,measurement.getRecordedAt());
        }).filter(Objects::nonNull).toList();
    }

    private Double getMetricValue(
            BodyMeasurement measurement,
            MeasurementMetric metric) {

        return switch (metric) {
            case WEIGHT -> measurement.getWeight();
            case BODY_FAT -> measurement.getBodyFatPercentage();
            case WAIST -> measurement.getWaist();
            case CHEST -> measurement.getChest();
            case LEFT_ARM -> measurement.getLeftArm();
            case RIGHT_ARM -> measurement.getRightArm();
            case LEFT_THIGH -> measurement.getLeftThigh();
            case RIGHT_THIGH -> measurement.getRightThigh();
        };
    }
}