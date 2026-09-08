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
import com.sangamesh.Fitsphere.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BodyMeasurementServiceImplTest {

    @Mock
    private BodyMeasurementRepository bodyMeasurementRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BodyMeasurementServiceImpl bodyMeasurementService;

    @AfterEach
    void clearContext() {
        clearInvocations(
                bodyMeasurementRepository,
                userService
        );
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        return user;
    }

    private BodyMeasurementRequestDto createRequest() {
        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        request.setWeight(70.0);
        request.setBodyFatPercentage(15.0);
        request.setWaist(80.0);
        request.setChest(100.0);
        request.setLeftArm(32.0);
        request.setRightArm(33.0);
        request.setLeftThigh(55.0);
        request.setRightThigh(56.0);
        request.setRecordedAt(
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        return request;
    }

    private BodyMeasurement createMeasurement() {
        BodyMeasurement measurement =
                new BodyMeasurement();

        measurement.setId(1L);
        measurement.setWeight(70.0);
        measurement.setBodyFatPercentage(15.0);
        measurement.setWaist(80.0);
        measurement.setChest(100.0);
        measurement.setLeftArm(32.0);
        measurement.setRightArm(33.0);
        measurement.setLeftThigh(55.0);
        measurement.setRightThigh(56.0);
        measurement.setRecordedAt(
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        return measurement;
    }

    @Test
    void addMeasurement_success() {

        User user = createUser();
        BodyMeasurementRequestDto request =
                createRequest();

        BodyMeasurement saved =
                createMeasurement();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findFirstByUserIdOrderByRecordedAtDesc(user.getId()))
                .thenReturn(Optional.empty());

        when(bodyMeasurementRepository.save(any(BodyMeasurement.class)))
                .thenReturn(saved);

        BodyMeasurementResponseDto result =
                bodyMeasurementService.addMeasurement(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(70.0, result.getWeight());
        assertEquals(15.0, result.getBodyFatPercentage());
        assertEquals(80.0, result.getWaist());
        assertEquals(100.0, result.getChest());
        assertEquals(32.0, result.getLeftArm());
        assertEquals(33.0, result.getRightArm());
        assertEquals(55.0, result.getLeftThigh());
        assertEquals(56.0, result.getRightThigh());

        verify(userService).getCurrentUser();
        verify(bodyMeasurementRepository)
                .findFirstByUserIdOrderByRecordedAtDesc(user.getId());
        verify(bodyMeasurementRepository)
                .save(any(BodyMeasurement.class));
    }

    @Test
    void addMeasurement_carriesForwardMissingValues() {

        User user = createUser();

        BodyMeasurement latest =
                createMeasurement();

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        request.setWeight(72.0);
        request.setRecordedAt(
                LocalDateTime.of(2026, 2, 1, 10, 0)
        );

        BodyMeasurement saved =
                createMeasurement();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findFirstByUserIdOrderByRecordedAtDesc(user.getId()))
                .thenReturn(Optional.of(latest));

        when(bodyMeasurementRepository.save(any(BodyMeasurement.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        BodyMeasurementResponseDto result =
                bodyMeasurementService.addMeasurement(request);

        assertEquals(72.0, result.getWeight());
        assertEquals(15.0, result.getBodyFatPercentage());
        assertEquals(80.0, result.getWaist());
        assertEquals(100.0, result.getChest());
        assertEquals(32.0, result.getLeftArm());
        assertEquals(33.0, result.getRightArm());
        assertEquals(55.0, result.getLeftThigh());
        assertEquals(56.0, result.getRightThigh());
    }

    @Test
    void addMeasurement_throwsWhenNoMeasurementProvided() {

        User user = createUser();

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        when(userService.getCurrentUser())
                .thenReturn(user);

        assertThrows(
                BadRequestException.class,
                () -> bodyMeasurementService.addMeasurement(request)
        );

        verify(bodyMeasurementRepository, never())
                .save(any(BodyMeasurement.class));
    }

    @Test
    void getMeasurementHistory_success() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        Page<BodyMeasurement> page =
                new PageImpl<>(List.of(measurement));

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtDescIdDesc(
                        eq(user.getId()),
                        any(PageRequest.class)))
                .thenReturn(page);

        Page<BodyMeasurementResponseDto> result =
                bodyMeasurementService.getMeasurementHistory(0);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(70.0,
                result.getContent().get(0).getWeight());

        verify(bodyMeasurementRepository)
                .findByUserIdOrderByRecordedAtDescIdDesc(
                        eq(user.getId()),
                        eq(PageRequest.of(0, 12))
                );
    }

    @Test
    void getLatestMeasurement_success() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findFirstByUserIdOrderByRecordedAtDescIdDesc(
                        user.getId()))
                .thenReturn(Optional.of(measurement));

        BodyMeasurementResponseDto result =
                bodyMeasurementService.getLatestMeasurement();

        assertEquals(1L, result.getId());
        assertEquals(70.0, result.getWeight());

        verify(bodyMeasurementRepository)
                .findFirstByUserIdOrderByRecordedAtDescIdDesc(
                        user.getId());
    }

    @Test
    void getLatestMeasurement_throwsWhenNoMeasurementExists() {

        User user = createUser();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findFirstByUserIdOrderByRecordedAtDescIdDesc(
                        user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bodyMeasurementService.getLatestMeasurement()
        );
    }

    @Test
    void deleteMeasurement_success() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.of(measurement));

        bodyMeasurementService.deleteMeasurement(1L);

        verify(bodyMeasurementRepository)
                .delete(measurement);
    }

    @Test
    void deleteMeasurement_throwsWhenNotFound() {

        User user = createUser();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bodyMeasurementService.deleteMeasurement(1L)
        );

        verify(bodyMeasurementRepository, never())
                .delete(any(BodyMeasurement.class));
    }

    @Test
    void updateMeasurement_success() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        request.setWeight(72.0);
        request.setWaist(82.0);

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.of(measurement));

        when(bodyMeasurementRepository.save(measurement))
                .thenReturn(measurement);

        BodyMeasurementResponseDto result =
                bodyMeasurementService.updateMeasurement(
                        1L,
                        request
                );

        assertEquals(72.0, measurement.getWeight());
        assertEquals(82.0, measurement.getWaist());

        // Other fields remain unchanged
        assertEquals(15.0,
                measurement.getBodyFatPercentage());
        assertEquals(100.0, measurement.getChest());

        assertEquals(72.0, result.getWeight());

        verify(bodyMeasurementRepository)
                .save(measurement);
    }

    @Test
    void updateMeasurement_updatesRecordedAt() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        LocalDateTime newDate =
                LocalDateTime.of(2026, 3, 1, 12, 0);

        request.setRecordedAt(newDate);
        request.setWeight(75.0);

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.of(measurement));

        when(bodyMeasurementRepository.save(measurement))
                .thenReturn(measurement);

        bodyMeasurementService.updateMeasurement(
                1L,
                request
        );

        assertEquals(newDate,
                measurement.getRecordedAt());
        assertEquals(75.0,
                measurement.getWeight());
    }

    @Test
    void updateMeasurement_throwsWhenMeasurementNotFound() {

        User user = createUser();

        BodyMeasurementRequestDto request =
                createRequest();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bodyMeasurementService.updateMeasurement(
                        1L,
                        request
                )
        );

        verify(bodyMeasurementRepository, never())
                .save(any(BodyMeasurement.class));
    }

    @Test
    void updateMeasurement_throwsWhenNoMeasurementProvided() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        BodyMeasurementRequestDto request =
                new BodyMeasurementRequestDto();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByIdAndUserId(1L, user.getId()))
                .thenReturn(Optional.of(measurement));

        assertThrows(
                BadRequestException.class,
                () -> bodyMeasurementService.updateMeasurement(
                        1L,
                        request
                )
        );

        verify(bodyMeasurementRepository, never())
                .save(any(BodyMeasurement.class));
    }

    @Test
    void getMeasurementTrend_returnsLatest12Measurements() {

        User user = createUser();

        List<BodyMeasurement> measurements =
                new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            BodyMeasurement measurement =
                    new BodyMeasurement();

            measurement.setId((long) i);
            measurement.setWeight(60.0 + i);
            measurement.setRecordedAt(
                    LocalDateTime.of(
                            2026,
                            1,
                            i,
                            10,
                            0
                    )
            );

            measurements.add(measurement);
        }

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtAscIdAsc(
                        user.getId()))
                .thenReturn(measurements);

        List<MeasurementTrendDto> result =
                bodyMeasurementService.getMeasurementTrend(
                        MeasurementMetric.WEIGHT
                );

        assertEquals(12, result.size());

        assertEquals(64.0,
                result.get(0).getValue());

        assertEquals(75.0,
                result.get(11).getValue());
    }

    @Test
    void getMeasurementTrend_filtersNullValues() {

        User user = createUser();

        BodyMeasurement first =
                createMeasurement();

        first.setWeight(null);

        BodyMeasurement second =
                createMeasurement();

        second.setId(2L);
        second.setWeight(75.0);

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtAscIdAsc(
                        user.getId()))
                .thenReturn(List.of(first, second));

        List<MeasurementTrendDto> result =
                bodyMeasurementService.getMeasurementTrend(
                        MeasurementMetric.WEIGHT
                );

        assertEquals(1, result.size());
        assertEquals(75.0,
                result.get(0).getValue());
    }

    @Test
    void getMeasurementTrend_supportsAllMetrics() {

        User user = createUser();

        BodyMeasurement measurement =
                createMeasurement();

        when(userService.getCurrentUser())
                .thenReturn(user);

        when(bodyMeasurementRepository
                .findByUserIdOrderByRecordedAtAscIdAsc(
                        user.getId()))
                .thenReturn(List.of(measurement));

        assertEquals(
                70.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.WEIGHT)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                15.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.BODY_FAT)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                80.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.WAIST)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                100.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.CHEST)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                32.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.LEFT_ARM)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                33.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.RIGHT_ARM)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                55.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.LEFT_THIGH)
                        .get(0)
                        .getValue()
        );

        assertEquals(
                56.0,
                bodyMeasurementService
                        .getMeasurementTrend(MeasurementMetric.RIGHT_THIGH)
                        .get(0)
                        .getValue()
        );
    }
}
