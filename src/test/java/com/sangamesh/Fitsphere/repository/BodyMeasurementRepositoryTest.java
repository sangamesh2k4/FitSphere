package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.BodyMeasurement;
import com.sangamesh.Fitsphere.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class BodyMeasurementRepositoryTest {

    @Autowired
    private BodyMeasurementRepository bodyMeasurementRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("password");
        user = userRepository.save(user);

        anotherUser = new User();
        anotherUser.setUsername("jane");
        anotherUser.setEmail("jane@gmail.com");
        anotherUser.setPassword("password");
        anotherUser = userRepository.save(anotherUser);
    }

    private BodyMeasurement createMeasurement(
            User user,
            double weight,
            LocalDateTime recordedAt) {

        BodyMeasurement measurement =
                new BodyMeasurement();

        measurement.setUser(user);
        measurement.setWeight(weight);
        measurement.setRecordedAt(recordedAt);

        return measurement;
    }

    @Test
    void findByUserIdOrderByRecordedAtDescIdDesc_returnsDescending() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        72.0,
                        LocalDateTime.of(2026, 1, 3, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        71.0,
                        LocalDateTime.of(2026, 1, 2, 10, 0)
                )
        );

        List<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtDescIdDesc(
                                user.getId()
                        );

        assertEquals(3, result.size());
        assertEquals(72.0, result.get(0).getWeight());
        assertEquals(71.0, result.get(1).getWeight());
        assertEquals(70.0, result.get(2).getWeight());
    }

    @Test
    void findByUserIdOrderByRecordedAtDescIdDesc_withPageable_returnsPage() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        71.0,
                        LocalDateTime.of(2026, 1, 2, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        72.0,
                        LocalDateTime.of(2026, 1, 3, 10, 0)
                )
        );

        Page<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtDescIdDesc(
                                user.getId(),
                                PageRequest.of(0, 2)
                        );

        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(72.0,
                result.getContent().get(0).getWeight());
        assertEquals(71.0,
                result.getContent().get(1).getWeight());
    }

    @Test
    void findByUserIdOrderByRecordedAtAscIdAsc_returnsAscending() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        72.0,
                        LocalDateTime.of(2026, 1, 3, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        71.0,
                        LocalDateTime.of(2026, 1, 2, 10, 0)
                )
        );

        List<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtAscIdAsc(
                                user.getId()
                        );

        assertEquals(3, result.size());
        assertEquals(70.0, result.get(0).getWeight());
        assertEquals(71.0, result.get(1).getWeight());
        assertEquals(72.0, result.get(2).getWeight());
    }

    @Test
    void findFirstByUserIdOrderByRecordedAtDescIdDesc_returnsLatest() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        75.0,
                        LocalDateTime.of(2026, 1, 5, 10, 0)
                )
        );

        Optional<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findFirstByUserIdOrderByRecordedAtDescIdDesc(
                                user.getId()
                        );

        assertTrue(result.isPresent());
        assertEquals(75.0, result.get().getWeight());
    }

    @Test
    void findFirstByUserIdOrderByRecordedAtDesc_returnsLatest() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        78.0,
                        LocalDateTime.of(2026, 1, 6, 10, 0)
                )
        );

        Optional<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findFirstByUserIdOrderByRecordedAtDesc(
                                user.getId()
                        );

        assertTrue(result.isPresent());
        assertEquals(78.0, result.get().getWeight());
    }

    @Test
    void findByIdAndUserId_returnsMeasurementForCorrectUser() {

        BodyMeasurement measurement =
                bodyMeasurementRepository.save(
                        createMeasurement(
                                user,
                                70.0,
                                LocalDateTime.of(2026, 1, 1, 10, 0)
                        )
                );

        Optional<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByIdAndUserId(
                                measurement.getId(),
                                user.getId()
                        );

        assertTrue(result.isPresent());
        assertEquals(70.0, result.get().getWeight());
    }

    @Test
    void findByIdAndUserId_doesNotReturnOtherUsersMeasurement() {

        BodyMeasurement measurement =
                bodyMeasurementRepository.save(
                        createMeasurement(
                                user,
                                70.0,
                                LocalDateTime.of(2026, 1, 1, 10, 0)
                        )
                );

        Optional<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByIdAndUserId(
                                measurement.getId(),
                                anotherUser.getId()
                        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdOrderByRecordedAtAsc_returnsAscending() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        75.0,
                        LocalDateTime.of(2026, 1, 3, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        72.0,
                        LocalDateTime.of(2026, 1, 2, 10, 0)
                )
        );

        List<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtAsc(
                                user.getId()
                        );

        assertEquals(3, result.size());
        assertEquals(70.0, result.get(0).getWeight());
        assertEquals(72.0, result.get(1).getWeight());
        assertEquals(75.0, result.get(2).getWeight());
    }

    @Test
    void repository_methods_onlyReturnMeasurementsForRequestedUser() {

        bodyMeasurementRepository.save(
                createMeasurement(
                        user,
                        70.0,
                        LocalDateTime.of(2026, 1, 1, 10, 0)
                )
        );

        bodyMeasurementRepository.save(
                createMeasurement(
                        anotherUser,
                        80.0,
                        LocalDateTime.of(2026, 1, 2, 10, 0)
                )
        );

        List<BodyMeasurement> result =
                bodyMeasurementRepository
                        .findByUserIdOrderByRecordedAtAsc(
                                user.getId()
                        );

        assertEquals(1, result.size());
        assertEquals(70.0, result.get(0).getWeight());
        assertEquals(user.getId(),
                result.get(0).getUser().getId());
    }
}