package com.sangamesh.Fitsphere.specification;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import org.springframework.data.jpa.domain.Specification;

public class ExerciseSpecification {

    public static Specification<Exercise> hasCategory(Category category){
        return (root, query, criteriaBuilder) -> {
            if(category==null){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("category"),category);
        };
    }

    public static Specification<Exercise> hasPrimaryMuscle(Muscle muscle) {
        return (root, query, cb) -> {
            if (muscle == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("primaryMuscle"), muscle);
        };
    }
    public static Specification<Exercise> hasEquipment(Equipment equipment) {
        return (root, query, cb) -> {
            if (equipment == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("equipment"), equipment);
        };
    }

    public static Specification<Exercise> hasDifficulty(Difficulty difficulty) {
        return (root, query, cb) -> {
            if (difficulty == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("difficulty"), difficulty);
        };
    }

    public static Specification<Exercise> hasExerciseType(ExerciseType exerciseType) {
        return (root, query, cb) -> {
            if (exerciseType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("exerciseType"), exerciseType);
        };
    }
    public static Specification<Exercise> hasKeyword(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.like(
                    cb.lower(root.get("name")),
                    pattern
            );
        };
    }

    public static Specification<Exercise> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Exercise> hasActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("active"), active);
        };
    }
}
