package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.WorkoutExercise;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import com.sangamesh.Fitsphere.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class WorkoutExerciseRepositoryTest {

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");

        return userRepository.save(user);
    }

    private Exercise saveExercise(String name) {
        Exercise exercise = Exercise.builder()
                .name(name)
                .category(Category.CHEST)
                .primaryMuscle(Muscle.MIDDLE_CHEST)
                .movementPattern(MovementPattern.HORIZONTAL_PUSH)
                .equipment(Equipment.BODYWEIGHT)
                .exerciseType(ExerciseType.COMPOUND)
                .difficulty(Difficulty.BEGINNER)
                .active(true)
                .build();

        return exerciseRepository.save(exercise);
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

    private WorkoutExercise saveWorkoutExercise(
            WorkoutSession workout,
            Exercise exercise,
            int order) {

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkoutSession(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setExerciseOrder(order);
        workoutExercise.setTotalVolume(0.0);

        return workoutExerciseRepository.save(workoutExercise);
    }

    @Test
    void findByIdAndWorkoutSession_User_Id_returnsWorkoutExerciseForCorrectUser() {
        User user = saveUser("user1", "user1@test.com");
        Exercise exercise = saveExercise("Push Up");
        WorkoutSession workout = saveWorkout(
                user,
                "Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                false
        );

        WorkoutExercise workoutExercise =
                saveWorkoutExercise(workout, exercise, 1);

        assertThat(
                workoutExerciseRepository
                        .findByIdAndWorkoutSession_User_Id(
                                workoutExercise.getId(),
                                user.getId()
                        )
        ).isPresent();
    }

    @Test
    void findByIdAndWorkoutSession_User_Id_doesNotReturnAnotherUsersWorkoutExercise() {
        User user1 = saveUser("user1", "user1@test.com");
        User user2 = saveUser("user2", "user2@test.com");

        Exercise exercise = saveExercise("Push Up");

        WorkoutSession workout = saveWorkout(
                user1,
                "Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                false
        );

        WorkoutExercise workoutExercise =
                saveWorkoutExercise(workout, exercise, 1);

        assertThat(
                workoutExerciseRepository
                        .findByIdAndWorkoutSession_User_Id(
                                workoutExercise.getId(),
                                user2.getId()
                        )
        ).isEmpty();
    }

    @Test
    void findByWorkoutSessionIdOrderByExerciseOrderAsc_returnsExercisesInOrder() {
        User user = saveUser("user1", "user1@test.com");

        Exercise pushUp = saveExercise("Push Up");
        Exercise benchPress = saveExercise("Bench Press");
        Exercise chestFly = saveExercise("Chest Fly");

        WorkoutSession workout = saveWorkout(
                user,
                "Chest Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                false
        );

        WorkoutExercise third =
                saveWorkoutExercise(workout, chestFly, 3);

        WorkoutExercise first =
                saveWorkoutExercise(workout, pushUp, 1);

        WorkoutExercise second =
                saveWorkoutExercise(workout, benchPress, 2);

        List<WorkoutExercise> result =
                workoutExerciseRepository
                        .findByWorkoutSessionIdOrderByExerciseOrderAsc(workout.getId());

        assertThat(result)
                .extracting(we -> we.getExercise().getName())
                .containsExactly(
                        "Push Up",
                        "Bench Press",
                        "Chest Fly"
                );
    }

    @Test
    void findByWorkoutSession_User_IdAndExercise_IdAndCompletedTrue_returnsCompletedWorkoutsForUserAndExerciseInAscendingOrder() {
        User user = saveUser("user1", "user1@test.com");

        Exercise pushUp = saveExercise("Push Up");
        Exercise benchPress = saveExercise("Bench Press");

        WorkoutSession olderWorkout = saveWorkout(
                user,
                "Older Chest Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession newerWorkout = saveWorkout(
                user,
                "Newer Chest Day",
                LocalDateTime.of(2026, 1, 10, 10, 0),
                true
        );

        WorkoutSession activeWorkout = saveWorkout(
                user,
                "Active Chest Day",
                LocalDateTime.of(2026, 1, 20, 10, 0),
                false
        );

        saveWorkoutExercise(olderWorkout, pushUp, 1);
        saveWorkoutExercise(newerWorkout, pushUp, 1);

        // Same user but different exercise — must not be returned.
        saveWorkoutExercise(newerWorkout, benchPress, 2);

        // Active workout — must not be returned.
        saveWorkoutExercise(activeWorkout, pushUp, 1);

        List<WorkoutExercise> result =
                workoutExerciseRepository
                        .findByWorkoutSession_User_IdAndExercise_IdAndWorkoutSession_CompletedTrueOrderByWorkoutSession_StartedAtAsc(
                                user.getId(),
                                pushUp.getId()
                        );

        assertThat(result)
                .extracting(we -> we.getWorkoutSession().getName())
                .containsExactly(
                        "Older Chest Day",
                        "Newer Chest Day"
                );
    }

    @Test
    void findByWorkoutSession_User_IdAndExercise_IdAndCompletedTrue_doesNotReturnAnotherUsersWorkout() {
        User user1 = saveUser("user1", "user1@test.com");
        User user2 = saveUser("user2", "user2@test.com");

        Exercise pushUp = saveExercise("Push Up");

        WorkoutSession user1Workout = saveWorkout(
                user1,
                "User 1 Chest",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession user2Workout = saveWorkout(
                user2,
                "User 2 Chest",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                true
        );

        saveWorkoutExercise(user1Workout, pushUp, 1);
        saveWorkoutExercise(user2Workout, pushUp, 1);

        List<WorkoutExercise> result =
                workoutExerciseRepository
                        .findByWorkoutSession_User_IdAndExercise_IdAndWorkoutSession_CompletedTrueOrderByWorkoutSession_StartedAtAsc(
                                user1.getId(),
                                pushUp.getId()
                        );

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(we -> we.getWorkoutSession().getUser().getId())
                .isEqualTo(user1.getId());
    }
}