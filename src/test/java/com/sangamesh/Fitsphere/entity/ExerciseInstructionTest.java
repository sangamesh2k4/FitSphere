package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseInstructionTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {

        ExerciseInstruction instruction =
                new ExerciseInstruction();

        assertNull(instruction.getId());
        assertNull(instruction.getStepNumber());
        assertNull(instruction.getInstruction());
        assertNull(instruction.getExercise());
    }

    @Test
    void allArgsConstructor_setsAllFields() {

        Exercise exercise = new Exercise();

        ExerciseInstruction instruction =
                new ExerciseInstruction(
                        1L,
                        2,
                        "Keep your back straight",
                        exercise
                );

        assertEquals(1L, instruction.getId());
        assertEquals(2, instruction.getStepNumber());
        assertEquals(
                "Keep your back straight",
                instruction.getInstruction()
        );
        assertSame(exercise, instruction.getExercise());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        ExerciseInstruction instruction =
                new ExerciseInstruction();

        Exercise exercise = new Exercise();

        instruction.setId(5L);
        instruction.setStepNumber(3);
        instruction.setInstruction("Press the weight upward");
        instruction.setExercise(exercise);

        assertEquals(5L, instruction.getId());
        assertEquals(3, instruction.getStepNumber());
        assertEquals(
                "Press the weight upward",
                instruction.getInstruction()
        );
        assertSame(exercise, instruction.getExercise());
    }
}