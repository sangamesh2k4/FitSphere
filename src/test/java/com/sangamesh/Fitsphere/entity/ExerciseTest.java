package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {

    @Test
    void noArgsConstructor_setsActiveTrue() {

        Exercise exercise = new Exercise();

        assertNull(exercise.getId());
        assertNull(exercise.getName());
        assertNull(exercise.getCategory());
        assertNull(exercise.getPrimaryMuscle());
        assertNull(exercise.getSecondaryMuscles());
        assertNull(exercise.getMovementPattern());
        assertNull(exercise.getEquipment());
        assertNull(exercise.getExerciseType());
        assertNull(exercise.getDifficulty());
        assertNull(exercise.getDescription());
        assertNull(exercise.getInstructions());
        assertNull(exercise.getTips());
        assertNull(exercise.getCommonMistakes());
        assertNull(exercise.getImageUrl());

        assertTrue(exercise.getActive());
    }

    @Test
    void allArgsConstructor_setsAllFields() {

        Long id = 1L;

        List<Muscle> secondaryMuscles =
                List.of(
                        Muscle.TRICEPS,
                        Muscle.FRONT_DELTS
                );

        List<ExerciseInstruction> instructions =
                List.of(new ExerciseInstruction());

        List<ExerciseTip> tips =
                List.of(new ExerciseTip());

        List<ExerciseCommonMistake> mistakes =
                List.of(new ExerciseCommonMistake());

        Exercise exercise = new Exercise(
                id,
                "Bench Press",
                Category.CHEST,
                Muscle.MIDDLE_CHEST,
                secondaryMuscles,
                MovementPattern.HORIZONTAL_PUSH,
                Equipment.BARBELL,
                ExerciseType.COMPOUND,
                Difficulty.INTERMEDIATE,
                "Chest pressing exercise",
                instructions,
                tips,
                mistakes,
                "bench.jpg",
                true
        );

        assertEquals(id, exercise.getId());
        assertEquals("Bench Press", exercise.getName());
        assertEquals(Category.CHEST, exercise.getCategory());
        assertEquals(
                Muscle.MIDDLE_CHEST,
                exercise.getPrimaryMuscle()
        );
        assertEquals(
                secondaryMuscles,
                exercise.getSecondaryMuscles()
        );
        assertEquals(
                MovementPattern.HORIZONTAL_PUSH,
                exercise.getMovementPattern()
        );
        assertEquals(
                Equipment.BARBELL,
                exercise.getEquipment()
        );
        assertEquals(
                ExerciseType.COMPOUND,
                exercise.getExerciseType()
        );
        assertEquals(
                Difficulty.INTERMEDIATE,
                exercise.getDifficulty()
        );
        assertEquals(
                "Chest pressing exercise",
                exercise.getDescription()
        );
        assertEquals(
                instructions,
                exercise.getInstructions()
        );
        assertEquals(
                tips,
                exercise.getTips()
        );
        assertEquals(
                mistakes,
                exercise.getCommonMistakes()
        );
        assertEquals("bench.jpg", exercise.getImageUrl());
        assertTrue(exercise.getActive());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        Exercise exercise = new Exercise();

        List<Muscle> secondaryMuscles =
                List.of(Muscle.TRICEPS);

        List<ExerciseInstruction> instructions =
                List.of(new ExerciseInstruction());

        List<ExerciseTip> tips =
                List.of(new ExerciseTip());

        List<ExerciseCommonMistake> mistakes =
                List.of(new ExerciseCommonMistake());

        exercise.setId(2L);
        exercise.setName("Squat");
        exercise.setCategory(Category.LEGS);
        exercise.setPrimaryMuscle(Muscle.QUADRICEPS);
        exercise.setSecondaryMuscles(secondaryMuscles);
        exercise.setMovementPattern(MovementPattern.SQUAT);
        exercise.setEquipment(Equipment.BARBELL);
        exercise.setExerciseType(ExerciseType.COMPOUND);
        exercise.setDifficulty(Difficulty.ADVANCED);
        exercise.setDescription("Leg exercise");
        exercise.setInstructions(instructions);
        exercise.setTips(tips);
        exercise.setCommonMistakes(mistakes);
        exercise.setImageUrl("squat.jpg");
        exercise.setActive(false);

        assertEquals(2L, exercise.getId());
        assertEquals("Squat", exercise.getName());
        assertEquals(Category.LEGS, exercise.getCategory());
        assertEquals(
                Muscle.QUADRICEPS,
                exercise.getPrimaryMuscle()
        );
        assertEquals(
                secondaryMuscles,
                exercise.getSecondaryMuscles()
        );
        assertEquals(
                MovementPattern.SQUAT,
                exercise.getMovementPattern()
        );
        assertEquals(
                Equipment.BARBELL,
                exercise.getEquipment()
        );
        assertEquals(
                ExerciseType.COMPOUND,
                exercise.getExerciseType()
        );
        assertEquals(
                Difficulty.ADVANCED,
                exercise.getDifficulty()
        );
        assertEquals(
                "Leg exercise",
                exercise.getDescription()
        );
        assertEquals(
                instructions,
                exercise.getInstructions()
        );
        assertEquals(
                tips,
                exercise.getTips()
        );
        assertEquals(
                mistakes,
                exercise.getCommonMistakes()
        );
        assertEquals("squat.jpg", exercise.getImageUrl());
        assertFalse(exercise.getActive());
    }

    @Test
    void builder_createsExerciseCorrectly() {

        Exercise exercise = Exercise.builder()
                .name("Bicep Curl")
                .category(Category.BICEPS)
                .primaryMuscle(Muscle.BICEPS)
                .secondaryMuscles(
                        List.of(Muscle.BRACHIALIS)
                )
                .movementPattern(MovementPattern.ELBOW_FLEXION)
                .equipment(Equipment.DUMBBELL)
                .exerciseType(ExerciseType.ISOLATION)
                .difficulty(Difficulty.BEGINNER)
                .description("Biceps exercise")
                .imageUrl("curl.jpg")
                .active(false)
                .build();

        assertEquals("Bicep Curl", exercise.getName());
        assertEquals(Category.BICEPS, exercise.getCategory());
        assertEquals(Muscle.BICEPS,
                exercise.getPrimaryMuscle());
        assertEquals(
                List.of(Muscle.BRACHIALIS),
                exercise.getSecondaryMuscles()
        );
        assertEquals(
                MovementPattern.ELBOW_FLEXION,
                exercise.getMovementPattern()
        );
        assertEquals(
                Equipment.DUMBBELL,
                exercise.getEquipment()
        );
        assertEquals(
                ExerciseType.ISOLATION,
                exercise.getExerciseType()
        );
        assertEquals(
                Difficulty.BEGINNER,
                exercise.getDifficulty()
        );
        assertEquals(
                "Biceps exercise",
                exercise.getDescription()
        );
        assertEquals("curl.jpg", exercise.getImageUrl());
        assertFalse(exercise.getActive());
    }
}