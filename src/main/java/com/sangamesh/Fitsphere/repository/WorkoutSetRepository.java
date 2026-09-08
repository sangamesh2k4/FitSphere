package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import com.sangamesh.Fitsphere.entity.WorkoutSet;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSetRepository
        extends JpaRepository<WorkoutSet, Long> {

    Optional<WorkoutSet>
    findByIdAndWorkoutExercise_WorkoutSession_User_Id(Long id, Long userId);

    List<WorkoutSet>
    findByWorkoutExerciseIdOrderBySetNumberAsc(Long workoutExerciseId);

    @Query("""
        SELECT MAX(ws.estimatedOneRepMax)
        FROM WorkoutSet ws
        WHERE ws.workoutExercise.exercise.id = :exerciseId
        AND ws.workoutExercise.workoutSession.user.id = :userId
        AND ws.workoutExercise.workoutSession.completed = true
        """)
    Optional<Double> findMaxEstimatedOneRepMax(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId);


    @Query("""
    SELECT MAX(ws.estimatedOneRepMax)
    FROM WorkoutSet ws
    WHERE ws.workoutExercise.exercise.id = :exerciseId
      AND ws.workoutExercise.workoutSession.user.id = :userId
      AND ws.workoutExercise.workoutSession.completed = true
      AND ws.workoutExercise.workoutSession.id <> :workoutId
    """)
    Optional<Double> findMaxEstimatedOneRepMaxExcludingWorkout(
            @Param("exerciseId") Long exerciseId,
            @Param("userId") Long userId,
            @Param("workoutId") Long workoutId
    );
}