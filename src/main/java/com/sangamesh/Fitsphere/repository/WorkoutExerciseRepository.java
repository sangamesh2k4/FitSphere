package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    Optional<WorkoutExercise> findByIdAndWorkoutSession_User_Id(Long id, Long userId);

    List<WorkoutExercise> findByWorkoutSessionIdOrderByExerciseOrderAsc(Long workoutSessionId);

    List<WorkoutExercise>
    findByWorkoutSession_User_IdAndExercise_IdAndWorkoutSession_CompletedTrueOrderByWorkoutSession_StartedAtAsc(Long userId, Long exerciseId);


}
