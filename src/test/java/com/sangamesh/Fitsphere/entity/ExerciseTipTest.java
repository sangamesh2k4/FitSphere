package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseTipTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {

        ExerciseTip tip = new ExerciseTip();

        assertNull(tip.getId());
        assertNull(tip.getTip());
        assertNull(tip.getExercise());
    }

    @Test
    void allArgsConstructor_setsAllFields() {

        Exercise exercise = new Exercise();

        ExerciseTip tip = new ExerciseTip(
                1L,
                "Keep your core tight",
                exercise
        );

        assertEquals(1L, tip.getId());
        assertEquals(
                "Keep your core tight",
                tip.getTip()
        );
        assertSame(exercise, tip.getExercise());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        ExerciseTip tip = new ExerciseTip();
        Exercise exercise = new Exercise();

        tip.setId(2L);
        tip.setTip("Control the movement");
        tip.setExercise(exercise);

        assertEquals(2L, tip.getId());
        assertEquals(
                "Control the movement",
                tip.getTip()
        );
        assertSame(exercise, tip.getExercise());
    }

    @Test
    void builder_createsEntityCorrectly() {

        Exercise exercise = new Exercise();

        ExerciseTip tip = ExerciseTip.builder()
                .id(3L)
                .tip("Avoid using momentum")
                .exercise(exercise)
                .build();

        assertEquals(3L, tip.getId());
        assertEquals(
                "Avoid using momentum",
                tip.getTip()
        );
        assertSame(exercise, tip.getExercise());
    }
}