package com.sangamesh.Fitsphere.config;

import com.sangamesh.Fitsphere.enums.Category;
import com.sangamesh.Fitsphere.enums.Muscle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExerciseCategoryMuscleMapping {

    private final Map<Category, List<Muscle>> mapping = Map.of(
            Category.CHEST, List.of(Muscle.UPPER_CHEST, Muscle.MIDDLE_CHEST, Muscle.LOWER_CHEST),
            Category.BACK, List.of(Muscle.LATS, Muscle.UPPER_BACK, Muscle.TRAPS, Muscle.LOWER_BACK),
            Category.SHOULDERS, List.of(Muscle.FRONT_DELTS, Muscle.SIDE_DELTS, Muscle.REAR_DELTS),
            Category.BICEPS, List.of(Muscle.BICEPS,Muscle.BRACHIALIS,Muscle.BRACHIORADIALIS),
            Category.TRICEPS, List.of(Muscle.TRICEPS),
            Category.LEGS, List.of(Muscle.QUADRICEPS, Muscle.HAMSTRINGS, Muscle.GLUTES,
                    Muscle.CALVES, Muscle.ADDUCTORS, Muscle.ABDUCTORS),
            Category.CORE, List.of(Muscle.UPPER_ABS, Muscle.LOWER_ABS, Muscle.OBLIQUES, Muscle.DEEP_CORE)
    );


    public List<Muscle> getPrimaryMuscles(Category category) {
        return mapping.getOrDefault(category, List.of());
    }
}