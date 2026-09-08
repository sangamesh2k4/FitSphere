package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkoutSetTest {

    @Test
    void defaultConstructor_setsPersonalRecordToFalse() {
        WorkoutSet workoutSet = new WorkoutSet();

        assertThat(workoutSet.getPersonalRecord()).isFalse();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        WorkoutExercise workoutExercise = new WorkoutExercise();

        WorkoutSet workoutSet = new WorkoutSet(
                1L,
                workoutExercise,
                2,
                80.0,
                10,
                800.0,
                106.67,
                true
        );

        assertThat(workoutSet.getId()).isEqualTo(1L);
        assertThat(workoutSet.getWorkoutExercise()).isSameAs(workoutExercise);
        assertThat(workoutSet.getSetNumber()).isEqualTo(2);
        assertThat(workoutSet.getWeight()).isEqualTo(80.0);
        assertThat(workoutSet.getReps()).isEqualTo(10);
        assertThat(workoutSet.getVolume()).isEqualTo(800.0);
        assertThat(workoutSet.getEstimatedOneRepMax()).isEqualTo(106.67);
        assertThat(workoutSet.getPersonalRecord()).isTrue();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        WorkoutSet workoutSet = new WorkoutSet();
        WorkoutExercise workoutExercise = new WorkoutExercise();

        workoutSet.setId(10L);
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setSetNumber(3);
        workoutSet.setWeight(100.0);
        workoutSet.setReps(5);
        workoutSet.setVolume(500.0);
        workoutSet.setEstimatedOneRepMax(116.67);
        workoutSet.setPersonalRecord(true);

        assertThat(workoutSet.getId()).isEqualTo(10L);
        assertThat(workoutSet.getWorkoutExercise()).isSameAs(workoutExercise);
        assertThat(workoutSet.getSetNumber()).isEqualTo(3);
        assertThat(workoutSet.getWeight()).isEqualTo(100.0);
        assertThat(workoutSet.getReps()).isEqualTo(5);
        assertThat(workoutSet.getVolume()).isEqualTo(500.0);
        assertThat(workoutSet.getEstimatedOneRepMax()).isEqualTo(116.67);
        assertThat(workoutSet.getPersonalRecord()).isTrue();
    }
}