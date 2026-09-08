package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.WorkoutExercise;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import com.sangamesh.Fitsphere.entity.WorkoutSet;
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
class WorkoutSetRepositoryTest {

    @Autowired
    private WorkoutSetRepository workoutSetRepository;

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

    private WorkoutSet saveSet(
            WorkoutExercise workoutExercise,
            int setNumber,
            double weight,
            int reps,
            double estimatedOneRepMax) {

        WorkoutSet set = new WorkoutSet();
        set.setWorkoutExercise(workoutExercise);
        set.setSetNumber(setNumber);
        set.setWeight(weight);
        set.setReps(reps);
        set.setVolume(weight * reps);
        set.setEstimatedOneRepMax(estimatedOneRepMax);
        set.setPersonalRecord(false);

        return workoutSetRepository.save(set);
    }

    @Test
    void findByIdAndWorkoutExercise_WorkoutSession_User_Id_returnsSetForCorrectUser() {
        User user = saveUser("user1", "user1@test.com");
        Exercise exercise = saveExercise("Push Up");

        WorkoutSession workout = saveWorkout(
                user,
                "Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutExercise workoutExercise =
                saveWorkoutExercise(workout, exercise, 1);

        WorkoutSet set =
                saveSet(workoutExercise, 1, 50.0, 10, 66.67);

        assertThat(
                workoutSetRepository
                        .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                                set.getId(),
                                user.getId()
                        )
        ).isPresent();
    }

    @Test
    void findByIdAndWorkoutExercise_WorkoutSession_User_Id_doesNotReturnAnotherUsersSet() {
        User user1 = saveUser("user1", "user1@test.com");
        User user2 = saveUser("user2", "user2@test.com");

        Exercise exercise = saveExercise("Push Up");

        WorkoutSession workout = saveWorkout(
                user1,
                "Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutExercise workoutExercise =
                saveWorkoutExercise(workout, exercise, 1);

        WorkoutSet set =
                saveSet(workoutExercise, 1, 50.0, 10, 66.67);

        assertThat(
                workoutSetRepository
                        .findByIdAndWorkoutExercise_WorkoutSession_User_Id(
                                set.getId(),
                                user2.getId()
                        )
        ).isEmpty();
    }

    @Test
    void findByWorkoutExerciseIdOrderBySetNumberAsc_returnsSetsInOrder() {
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

        saveSet(workoutExercise, 3, 70.0, 8, 88.67);
        saveSet(workoutExercise, 1, 50.0, 10, 66.67);
        saveSet(workoutExercise, 2, 60.0, 10, 80.0);

        List<WorkoutSet> result =
                workoutSetRepository
                        .findByWorkoutExerciseIdOrderBySetNumberAsc(
                                workoutExercise.getId()
                        );

        assertThat(result)
                .extracting(WorkoutSet::getSetNumber)
                .containsExactly(1, 2, 3);
    }

    @Test
    void findMaxEstimatedOneRepMax_returnsHighestValueForCompletedWorkouts() {
        User user = saveUser("user1", "user1@test.com");
        Exercise pushUp = saveExercise("Push Up");

        WorkoutSession workout1 = saveWorkout(
                user,
                "Push Day 1",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession workout2 = saveWorkout(
                user,
                "Push Day 2",
                LocalDateTime.of(2026, 1, 10, 10, 0),
                true
        );

        WorkoutSession activeWorkout = saveWorkout(
                user,
                "Active Push Day",
                LocalDateTime.of(2026, 1, 20, 10, 0),
                false
        );

        WorkoutExercise exercise1 =
                saveWorkoutExercise(workout1, pushUp, 1);

        WorkoutExercise exercise2 =
                saveWorkoutExercise(workout2, pushUp, 1);

        WorkoutExercise activeExercise =
                saveWorkoutExercise(activeWorkout, pushUp, 1);

        saveSet(exercise1, 1, 50.0, 10, 66.67);
        saveSet(exercise2, 1, 60.0, 10, 80.0);

        // Higher value, but active workout must be ignored.
        saveSet(activeExercise, 1, 100.0, 10, 133.33);

        Double result =
                workoutSetRepository
                        .findMaxEstimatedOneRepMax(
                                pushUp.getId(),
                                user.getId()
                        )
                        .orElse(null);

        assertThat(result).isEqualTo(80.0);
    }

    @Test
    void findMaxEstimatedOneRepMax_doesNotReturnAnotherUsersValue() {
        User user1 = saveUser("user1", "user1@test.com");
        User user2 = saveUser("user2", "user2@test.com");

        Exercise pushUp = saveExercise("Push Up");

        WorkoutSession user1Workout = saveWorkout(
                user1,
                "User 1 Push",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession user2Workout = saveWorkout(
                user2,
                "User 2 Push",
                LocalDateTime.of(2026, 1, 2, 10, 0),
                true
        );

        WorkoutExercise exercise1 =
                saveWorkoutExercise(user1Workout, pushUp, 1);

        WorkoutExercise exercise2 =
                saveWorkoutExercise(user2Workout, pushUp, 1);

        saveSet(exercise1, 1, 50.0, 10, 66.67);
        saveSet(exercise2, 1, 100.0, 10, 133.33);

        Double result =
                workoutSetRepository
                        .findMaxEstimatedOneRepMax(
                                pushUp.getId(),
                                user1.getId()
                        )
                        .orElse(null);

        assertThat(result).isEqualTo(66.67);
    }

    @Test
    void findMaxEstimatedOneRepMax_doesNotReturnDifferentExerciseValue() {
        User user = saveUser("user1", "user1@test.com");

        Exercise pushUp = saveExercise("Push Up");
        Exercise benchPress = saveExercise("Bench Press");

        WorkoutSession workout = saveWorkout(
                user,
                "Chest Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutExercise pushUpExercise =
                saveWorkoutExercise(workout, pushUp, 1);

        WorkoutExercise benchPressExercise =
                saveWorkoutExercise(workout, benchPress, 2);

        saveSet(pushUpExercise, 1, 50.0, 10, 66.67);

        // Higher value but different exercise.
        saveSet(benchPressExercise, 1, 100.0, 10, 133.33);

        Double result =
                workoutSetRepository
                        .findMaxEstimatedOneRepMax(
                                pushUp.getId(),
                                user.getId()
                        )
                        .orElse(null);

        assertThat(result).isEqualTo(66.67);
    }

    @Test
    void findMaxEstimatedOneRepMaxExcludingWorkout_excludesSpecifiedWorkout() {
        User user = saveUser("user1", "user1@test.com");
        Exercise pushUp = saveExercise("Push Up");

        WorkoutSession oldWorkout = saveWorkout(
                user,
                "Old Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutSession currentWorkout = saveWorkout(
                user,
                "Current Push Day",
                LocalDateTime.of(2026, 1, 10, 10, 0),
                true
        );

        WorkoutExercise oldExercise =
                saveWorkoutExercise(oldWorkout, pushUp, 1);

        WorkoutExercise currentExercise =
                saveWorkoutExercise(currentWorkout, pushUp, 1);

        saveSet(oldExercise, 1, 50.0, 10, 66.67);
        saveSet(currentExercise, 1, 80.0, 10, 106.67);

        Double result =
                workoutSetRepository
                        .findMaxEstimatedOneRepMaxExcludingWorkout(
                                pushUp.getId(),
                                user.getId(),
                                currentWorkout.getId()
                        )
                        .orElse(null);

        assertThat(result).isEqualTo(66.67);
    }

    @Test
    void findMaxEstimatedOneRepMaxExcludingWorkout_returnsEmptyWhenNoOtherCompletedWorkoutExists() {
        User user = saveUser("user1", "user1@test.com");
        Exercise pushUp = saveExercise("Push Up");

        WorkoutSession workout = saveWorkout(
                user,
                "Push Day",
                LocalDateTime.of(2026, 1, 1, 10, 0),
                true
        );

        WorkoutExercise workoutExercise =
                saveWorkoutExercise(workout, pushUp, 1);

        saveSet(workoutExercise, 1, 50.0, 10, 66.67);

        assertThat(
                workoutSetRepository
                        .findMaxEstimatedOneRepMaxExcludingWorkout(
                                pushUp.getId(),
                                user.getId(),
                                workout.getId()
                        )
        ).isEmpty();
    }
}