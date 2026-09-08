package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    private Exercise benchPress;
    private Exercise squat;
    private Exercise inactiveExercise;

    @BeforeEach
    void setUp() {

        benchPress = createExercise(
                "Bench Press",
                Category.CHEST,
                Muscle.MIDDLE_CHEST,
                Equipment.BARBELL,
                Difficulty.INTERMEDIATE,
                ExerciseType.COMPOUND,
                MovementPattern.HORIZONTAL_PUSH,
                true
        );

        squat = createExercise(
                "Squat",
                Category.LEGS,
                Muscle.QUADRICEPS,
                Equipment.BARBELL,
                Difficulty.ADVANCED,
                ExerciseType.COMPOUND,
                MovementPattern.SQUAT,
                true
        );

        inactiveExercise = createExercise(
                "Old Bench Press",
                Category.CHEST,
                Muscle.MIDDLE_CHEST,
                Equipment.BARBELL,
                Difficulty.INTERMEDIATE,
                ExerciseType.COMPOUND,
                MovementPattern.HORIZONTAL_PUSH,
                false
        );

        exerciseRepository.save(benchPress);
        exerciseRepository.save(squat);
        exerciseRepository.save(inactiveExercise);
    }

    private Exercise createExercise(
            String name,
            Category category,
            Muscle muscle,
            Equipment equipment,
            Difficulty difficulty,
            ExerciseType exerciseType,
            MovementPattern movementPattern,
            boolean active) {

        Exercise exercise = new Exercise();

        exercise.setName(name);
        exercise.setCategory(category);
        exercise.setPrimaryMuscle(muscle);
        exercise.setEquipment(equipment);
        exercise.setDifficulty(difficulty);
        exercise.setExerciseType(exerciseType);
        exercise.setMovementPattern(movementPattern);
        exercise.setActive(active);

        return exercise;
    }

    @Test
    void findByName_success() {

        Optional<Exercise> result =
                exerciseRepository.findByName("Bench Press");

        assertTrue(result.isPresent());
        assertEquals("Bench Press",
                result.get().getName());
    }

    @Test
    void findByName_notFound() {

        Optional<Exercise> result =
                exerciseRepository.findByName("Deadlift");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByName_success() {

        assertTrue(
                exerciseRepository.existsByName("Bench Press")
        );

        assertFalse(
                exerciseRepository.existsByName("Deadlift")
        );
    }

    @Test
    void findByCategory_success() {

        List<Exercise> result =
                exerciseRepository.findByCategory(
                        Category.CHEST);

        assertEquals(2, result.size());
    }

    @Test
    void findByCategoryAndPrimaryMuscleAndActiveTrue_success() {

        List<Exercise> result =
                exerciseRepository
                        .findByCategoryAndPrimaryMuscleAndActiveTrue(
                                Category.CHEST,
                                Muscle.MIDDLE_CHEST
                        );

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void findByCategoryAndPrimaryMuscleAndActiveTrue_excludesInactive() {

        List<Exercise> result =
                exerciseRepository
                        .findByCategoryAndPrimaryMuscleAndActiveTrue(
                                Category.CHEST,
                                Muscle.MIDDLE_CHEST
                        );

        assertTrue(
                result.stream()
                        .allMatch(Exercise::getActive)
        );
    }

    @Test
    void findByPrimaryMuscle_success() {

        List<Exercise> result =
                exerciseRepository.findByPrimaryMuscle(
                        Muscle.MIDDLE_CHEST);

        assertEquals(2, result.size());
    }

    @Test
    void findByEquipment_success() {

        List<Exercise> result =
                exerciseRepository.findByEquipment(
                        Equipment.BARBELL);

        assertEquals(3, result.size());
    }

    @Test
    void findByDifficulty_success() {

        List<Exercise> result =
                exerciseRepository.findByDifficulty(
                        Difficulty.INTERMEDIATE);

        assertEquals(2, result.size());
    }

    @Test
    void findByExerciseType_success() {

        List<Exercise> result =
                exerciseRepository.findByExerciseType(
                        ExerciseType.COMPOUND);

        assertEquals(3, result.size());
    }

    @Test
    void findByMovementPattern_success() {

        List<Exercise> result =
                exerciseRepository.findByMovementPattern(
                        MovementPattern.HORIZONTAL_PUSH);

        assertEquals(2, result.size());
    }

    @Test
    void findByNameContainingIgnoreCase_success() {

        List<Exercise> result =
                exerciseRepository
                        .findByNameContainingIgnoreCase("bench");

        assertEquals(2, result.size());
    }

    @Test
    void findByActiveTrue_success() {

        List<Exercise> result =
                exerciseRepository.findByActiveTrue();

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(Exercise::getActive)
        );
    }

    @Test
    void findByIdAndActiveTrue_success() {

        Optional<Exercise> result =
                exerciseRepository.findByIdAndActiveTrue(
                        benchPress.getId());

        assertTrue(result.isPresent());
        assertEquals("Bench Press",
                result.get().getName());
    }

    @Test
    void findByIdAndActiveTrue_returnsEmptyForInactive() {

        Optional<Exercise> result =
                exerciseRepository.findByIdAndActiveTrue(
                        inactiveExercise.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void countByActiveTrue_success() {

        assertEquals(
                2,
                exerciseRepository.countByActiveTrue()
        );
    }

    @Test
    void countByActiveFalse_success() {

        assertEquals(
                1,
                exerciseRepository.countByActiveFalse()
        );
    }

    @Test
    void findByActiveTrue_pageable_success() {

        Pageable pageable =
                PageRequest.of(
                        0,
                        1,
                        Sort.by("name").ascending()
                );

        Page<Exercise> result =
                exerciseRepository.findByActiveTrue(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals("Bench Press",
                result.getContent().get(0).getName());
    }

    @Test
    void findByCategoryAndActiveTrue_success() {

        List<Exercise> result =
                exerciseRepository.findByCategoryAndActiveTrue(
                        Category.CHEST);

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void findByPrimaryMuscleAndActiveTrue_success() {

        List<Exercise> result =
                exerciseRepository.findByPrimaryMuscleAndActiveTrue(
                        Muscle.MIDDLE_CHEST);

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_success() {

        List<Exercise> result =
                exerciseRepository
                        .findByNameContainingIgnoreCaseAndActiveTrue(
                                "bench");

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void findByNameContainingIgnoreCaseAndActiveTrue_excludesInactive() {

        List<Exercise> result =
                exerciseRepository
                        .findByNameContainingIgnoreCaseAndActiveTrue(
                                "bench");

        assertTrue(
                result.stream()
                        .allMatch(Exercise::getActive)
        );

        assertEquals(
                1,
                result.size()
        );
    }
}