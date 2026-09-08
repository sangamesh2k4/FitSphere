package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseCommonMistakeTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {

        ExerciseCommonMistake mistake =
                new ExerciseCommonMistake();

        assertNull(mistake.getId());
        assertNull(mistake.getMistake());
        assertNull(mistake.getExercise());
    }

    @Test
    void allArgsConstructor_setsAllFields() {

        Exercise exercise = new Exercise();

        ExerciseCommonMistake mistake =
                new ExerciseCommonMistake(
                        1L,
                        "Using excessive weight",
                        exercise
                );

        assertEquals(1L, mistake.getId());
        assertEquals(
                "Using excessive weight",
                mistake.getMistake()
        );
        assertSame(exercise, mistake.getExercise());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        ExerciseCommonMistake mistake =
                new ExerciseCommonMistake();

        Exercise exercise = new Exercise();

        mistake.setId(2L);
        mistake.setMistake("Incorrect posture");
        mistake.setExercise(exercise);

        assertEquals(2L, mistake.getId());
        assertEquals(
                "Incorrect posture",
                mistake.getMistake()
        );
        assertSame(exercise, mistake.getExercise());
    }

    @Test
    void builder_createsEntityCorrectly() {

        Exercise exercise = new Exercise();

        ExerciseCommonMistake mistake =
                ExerciseCommonMistake.builder()
                        .id(3L)
                        .mistake("Using momentum")
                        .exercise(exercise)
                        .build();

        assertEquals(3L, mistake.getId());
        assertEquals(
                "Using momentum",
                mistake.getMistake()
        );
        assertSame(exercise, mistake.getExercise());
    }
}