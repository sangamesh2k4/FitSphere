package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class WorkoutSessionRepositoryTest {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        return userRepository.save(user);
    }

    private WorkoutSession saveWorkout(
            User user,
            String name,
            LocalDateTime startedAt,
            boolean completed) {

        WorkoutSession workout = new WorkoutSession();
        workout.setUser(user);
        workout.setName(name);
        workout.setStartedAt(startedAt);
        workout.setCompleted(completed);

        if (completed) {
            workout.setCompletedAt(startedAt.plusHours(1));
        }

        return workoutSessionRepository.save(workout);
    }

    @Test
    void findByIdAndUser_returnsWorkoutForCorrectUser() {
        User user = saveUser("user1", "user1@test.com");

        WorkoutSession workout = saveWorkout(
                user,
                "Push Day",
                LocalDateTime.now(),
                false
        );

        assertThat(
                workoutSessionRepository.findByIdAndUser(workout.getId(), user)
        ).isPresent();
    }

    @Test
    void findByIdAndUser_doesNotReturnAnotherUsersWorkout() {
        User user1 = saveUser("user1", "user1@test.com");
        User user2 = saveUser("user2", "user2@test.com");

        WorkoutSession workout = saveWorkout(
                user1,
                "Push Day",
                LocalDateTime.now(),
                false
        );

        assertThat(
                workoutSessionRepository.findByIdAndUser(workout.getId(), user2)
        ).isEmpty();
    }

    @Test
    void findByUserOrderByStartedAtDesc_returnsNewestFirst() {
        User user = saveUser("user1", "user1@test.com");

        WorkoutSession older = saveWorkout(
                user,
                "Old Workout",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession newer = saveWorkout(
                user,
                "New Workout",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                true
        );

        List<WorkoutSession> result =
                workoutSessionRepository.findByUserOrderByStartedAtDesc(user);

        assertThat(result)
                .extracting(WorkoutSession::getId)
                .containsExactly(newer.getId(), older.getId());
    }

    @Test
    void findFirstByUserAndCompletedFalseOrderByStartedAtDesc_returnsLatestActiveWorkout() {
        User user = saveUser("user1", "user1@test.com");

        WorkoutSession olderActive = saveWorkout(
                user,
                "Older Active",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                false
        );

        WorkoutSession newerActive = saveWorkout(
                user,
                "Newer Active",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                false
        );

        WorkoutSession completed = saveWorkout(
                user,
                "Completed",
                LocalDateTime.of(2026, 1, 3, 10, 0),
                true
        );

        assertThat(
                workoutSessionRepository
                        .findFirstByUserAndCompletedFalseOrderByStartedAtDesc(user)
        )
                .isPresent()
                .get()
                .extracting(WorkoutSession::getId)
                .isEqualTo(newerActive.getId());
    }

    @Test
    void findByUserAndCompletedTrueOrderByStartedAtDesc_returnsOnlyCompletedWorkouts() {
        User user = saveUser("user1", "user1@test.com");

        WorkoutSession completed1 = saveWorkout(
                user,
                "Completed 1",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession completed2 = saveWorkout(
                user,
                "Completed 2",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                true
        );

        saveWorkout(
                user,
                "Active",
                LocalDateTime.of(2026, 1, 3, 10, 0),
                false
        );

        List<WorkoutSession> result =
                workoutSessionRepository
                        .findByUserAndCompletedTrueOrderByStartedAtDesc(user);

        assertThat(result)
                .extracting(WorkoutSession::getId)
                .containsExactly(completed2.getId(), completed1.getId());
    }

    @Test
    void findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc_filtersByDateRange() {
        User user = saveUser("user1", "user1@test.com");

        WorkoutSession before = saveWorkout(
                user,
                "Before",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession inside1 = saveWorkout(
                user,
                "Inside 1",
                LocalDateTime.of(2026, 1, 10, 10, 0),
                true
        );

        WorkoutSession inside2 = saveWorkout(
                user,
                "Inside 2",
                LocalDateTime.of(2026, 1, 15, 10, 0),
                true
        );

        WorkoutSession after = saveWorkout(
                user,
                "After",
                LocalDateTime.of(2026, 2, 1, 10, 0),
                true
        );

        List<WorkoutSession> result =
                workoutSessionRepository
                        .findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(
                                user,
                                LocalDateTime.of(2026, 1, 5, 0, 0),
                                LocalDateTime.of(2026, 1, 20, 23, 59)
                        );

        assertThat(result)
                .extracting(WorkoutSession::getId)
                .containsExactly(inside1.getId(), inside2.getId());
    }
}