package com.sangamesh.Fitsphere.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkoutSessionTest {

    @Test
    void defaultConstructor_initializesDefaults() {
        WorkoutSession workoutSession = new WorkoutSession();

        assertThat(workoutSession.getCompleted()).isFalse();
        assertThat(workoutSession.getWorkoutExercises()).isNotNull();
        assertThat(workoutSession.getWorkoutExercises()).isEmpty();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        User user = new User();
        List<WorkoutExercise> exercises = new ArrayList<>();

        LocalDateTime startedAt =
                LocalDateTime.of(2026, 1, 1, 10, 0);

        LocalDateTime completedAt =
                LocalDateTime.of(2026, 1, 1, 11, 0);

        WorkoutSession workoutSession = new WorkoutSession(
                1L,
                user,
                "Push Day",
                startedAt,
                completedAt,
                true,
                1500.0,
                exercises
        );

        assertThat(workoutSession.getId()).isEqualTo(1L);
        assertThat(workoutSession.getUser()).isSameAs(user);
        assertThat(workoutSession.getName()).isEqualTo("Push Day");
        assertThat(workoutSession.getStartedAt()).isEqualTo(startedAt);
        assertThat(workoutSession.getCompletedAt()).isEqualTo(completedAt);
        assertThat(workoutSession.getCompleted()).isTrue();
        assertThat(workoutSession.getTotalVolume()).isEqualTo(1500.0);
        assertThat(workoutSession.getWorkoutExercises())
                .isSameAs(exercises);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        WorkoutSession workoutSession = new WorkoutSession();

        User user = new User();
        List<WorkoutExercise> exercises = new ArrayList<>();

        LocalDateTime startedAt =
                LocalDateTime.of(2026, 2, 1, 10, 0);

        LocalDateTime completedAt =
                LocalDateTime.of(2026, 2, 1, 11, 0);

        workoutSession.setId(10L);
        workoutSession.setUser(user);
        workoutSession.setName("Leg Day");
        workoutSession.setStartedAt(startedAt);
        workoutSession.setCompletedAt(completedAt);
        workoutSession.setCompleted(true);
        workoutSession.setTotalVolume(2000.0);
        workoutSession.setWorkoutExercises(exercises);

        assertThat(workoutSession.getId()).isEqualTo(10L);
        assertThat(workoutSession.getUser()).isSameAs(user);
        assertThat(workoutSession.getName()).isEqualTo("Leg Day");
        assertThat(workoutSession.getStartedAt()).isEqualTo(startedAt);
        assertThat(workoutSession.getCompletedAt()).isEqualTo(completedAt);
        assertThat(workoutSession.getCompleted()).isTrue();
        assertThat(workoutSession.getTotalVolume()).isEqualTo(2000.0);
        assertThat(workoutSession.getWorkoutExercises())
                .isSameAs(exercises);
    }
}