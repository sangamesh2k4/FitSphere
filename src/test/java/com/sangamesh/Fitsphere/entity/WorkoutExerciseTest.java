package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkoutExerciseTest {

    @Test
    void defaultConstructor_initializesSets() {
        WorkoutExercise workoutExercise = new WorkoutExercise();

        assertThat(workoutExercise.getSets()).isNotNull();
        assertThat(workoutExercise.getSets()).isEmpty();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        WorkoutSession workoutSession = new WorkoutSession();
        Exercise exercise = new Exercise();

        List<WorkoutSet> sets = new ArrayList<>();

        WorkoutExercise workoutExercise = new WorkoutExercise(
                1L,
                workoutSession,
                exercise,
                2,
                800.0,
                sets
        );

        assertThat(workoutExercise.getId()).isEqualTo(1L);
        assertThat(workoutExercise.getWorkoutSession()).isSameAs(workoutSession);
        assertThat(workoutExercise.getExercise()).isSameAs(exercise);
        assertThat(workoutExercise.getExerciseOrder()).isEqualTo(2);
        assertThat(workoutExercise.getTotalVolume()).isEqualTo(800.0);
        assertThat(workoutExercise.getSets()).isSameAs(sets);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        WorkoutExercise workoutExercise = new WorkoutExercise();

        WorkoutSession workoutSession = new WorkoutSession();
        Exercise exercise = new Exercise();
        List<WorkoutSet> sets = new ArrayList<>();

        workoutExercise.setId(10L);
        workoutExercise.setWorkoutSession(workoutSession);
        workoutExercise.setExercise(exercise);
        workoutExercise.setExerciseOrder(3);
        workoutExercise.setTotalVolume(1200.0);
        workoutExercise.setSets(sets);

        assertThat(workoutExercise.getId()).isEqualTo(10L);
        assertThat(workoutExercise.getWorkoutSession()).isSameAs(workoutSession);
        assertThat(workoutExercise.getExercise()).isSameAs(exercise);
        assertThat(workoutExercise.getExerciseOrder()).isEqualTo(3);
        assertThat(workoutExercise.getTotalVolume()).isEqualTo(1200.0);
        assertThat(workoutExercise.getSets()).isSameAs(sets);
    }
}