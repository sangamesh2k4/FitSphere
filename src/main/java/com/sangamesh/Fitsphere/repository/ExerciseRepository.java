package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise ,Long>, JpaSpecificationExecutor<Exercise> {

    Optional<Exercise> findByName(String name);

    boolean existsByName(String name);

    List<Exercise> findByCategory(Category category);

    List<Exercise> findByCategoryAndPrimaryMuscleAndActiveTrue(Category category, Muscle primaryMuscle);

    List<Exercise> findByPrimaryMuscle(Muscle muscle);

    List<Exercise> findByEquipment(Equipment equipment);

    List<Exercise> findByDifficulty(Difficulty difficulty);

    List<Exercise> findByExerciseType(ExerciseType exerciseType);

    List<Exercise> findByMovementPattern(MovementPattern movementPattern);

    List<Exercise> findByNameContainingIgnoreCase(String keyword);

    List<Exercise> findByActiveTrue();

    Optional<Exercise> findByIdAndActiveTrue(Long id);

    long countByActiveTrue();

    long countByActiveFalse();

    Page<Exercise> findByActiveTrue(Pageable pageable);

    List<Exercise> findByCategoryAndActiveTrue(Category category);

    List<Exercise> findByPrimaryMuscleAndActiveTrue(Muscle muscle);

    List<Exercise> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);







}
