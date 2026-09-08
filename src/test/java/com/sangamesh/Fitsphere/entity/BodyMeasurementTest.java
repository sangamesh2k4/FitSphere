package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BodyMeasurementTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {

        BodyMeasurement measurement =
                new BodyMeasurement();

        assertNull(measurement.getId());
        assertNull(measurement.getUser());
        assertNull(measurement.getWeight());
        assertNull(measurement.getBodyFatPercentage());
        assertNull(measurement.getWaist());
        assertNull(measurement.getChest());
        assertNull(measurement.getLeftArm());
        assertNull(measurement.getRightArm());
        assertNull(measurement.getLeftThigh());
        assertNull(measurement.getRightThigh());
        assertNull(measurement.getRecordedAt());
    }

    @Test
    void allArgsConstructor_setsAllFields() {

        Long id = 1L;
        User user = new User();

        LocalDateTime recordedAt =
                LocalDateTime.of(2026, 1, 1, 10, 0);

        BodyMeasurement measurement =
                new BodyMeasurement(
                        id,
                        user,
                        70.0,
                        15.0,
                        80.0,
                        100.0,
                        32.0,
                        33.0,
                        55.0,
                        56.0,
                        recordedAt
                );

        assertEquals(id, measurement.getId());
        assertEquals(user, measurement.getUser());
        assertEquals(70.0, measurement.getWeight());
        assertEquals(15.0,
                measurement.getBodyFatPercentage());
        assertEquals(80.0, measurement.getWaist());
        assertEquals(100.0, measurement.getChest());
        assertEquals(32.0, measurement.getLeftArm());
        assertEquals(33.0, measurement.getRightArm());
        assertEquals(55.0, measurement.getLeftThigh());
        assertEquals(56.0, measurement.getRightThigh());
        assertEquals(recordedAt,
                measurement.getRecordedAt());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        BodyMeasurement measurement =
                new BodyMeasurement();

        User user = new User();

        LocalDateTime recordedAt =
                LocalDateTime.of(2026, 2, 1, 12, 0);

        measurement.setId(2L);
        measurement.setUser(user);
        measurement.setWeight(72.0);
        measurement.setBodyFatPercentage(14.5);
        measurement.setWaist(82.0);
        measurement.setChest(102.0);
        measurement.setLeftArm(33.0);
        measurement.setRightArm(34.0);
        measurement.setLeftThigh(56.0);
        measurement.setRightThigh(57.0);
        measurement.setRecordedAt(recordedAt);

        assertEquals(2L, measurement.getId());
        assertEquals(user, measurement.getUser());
        assertEquals(72.0, measurement.getWeight());
        assertEquals(14.5,
                measurement.getBodyFatPercentage());
        assertEquals(82.0, measurement.getWaist());
        assertEquals(102.0, measurement.getChest());
        assertEquals(33.0, measurement.getLeftArm());
        assertEquals(34.0, measurement.getRightArm());
        assertEquals(56.0, measurement.getLeftThigh());
        assertEquals(57.0, measurement.getRightThigh());
        assertEquals(recordedAt,
                measurement.getRecordedAt());
    }

    @Test
    void prePersist_setsRecordedAtWhenNull() {

        BodyMeasurement measurement =
                new BodyMeasurement();

        assertNull(measurement.getRecordedAt());

        measurement.prePersist();

        assertNotNull(measurement.getRecordedAt());
    }

    @Test
    void prePersist_doesNotOverwriteExistingRecordedAt() {

        BodyMeasurement measurement =
                new BodyMeasurement();

        LocalDateTime recordedAt =
                LocalDateTime.of(2026, 3, 1, 10, 0);

        measurement.setRecordedAt(recordedAt);

        measurement.prePersist();

        assertEquals(recordedAt,
                measurement.getRecordedAt());
    }
}