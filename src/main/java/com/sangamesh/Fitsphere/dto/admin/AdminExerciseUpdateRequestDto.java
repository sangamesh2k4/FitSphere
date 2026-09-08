package com.sangamesh.Fitsphere.dto.admin;

import com.sangamesh.Fitsphere.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class AdminExerciseUpdateRequestDto {

    private String name;
    private Category category;
    private Muscle primaryMuscle;
    private List<Muscle> secondaryMuscles;
    private MovementPattern movementPattern;
    private Equipment equipment;
    private ExerciseType exerciseType;
    private Difficulty difficulty;
    private String description;
    private List<String> instructions;
    private List<String> tips;
    private List<String> commonMistakes;
    private String imageUrl;
}