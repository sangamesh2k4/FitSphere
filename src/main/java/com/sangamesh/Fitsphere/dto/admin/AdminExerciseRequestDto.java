package com.sangamesh.Fitsphere.dto.admin;

import com.sangamesh.Fitsphere.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdminExerciseRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private Category category;

    @NotNull
    private Muscle primaryMuscle;

    private List<Muscle> secondaryMuscles;

    @NotNull
    private MovementPattern movementPattern;

    @NotNull
    private Equipment equipment;

    @NotNull
    private ExerciseType exerciseType;

    @NotNull
    private Difficulty difficulty;

    private String description;

    private List<String> instructions;

    private List<String> tips;

    private List<String> commonMistakes;

    private String imageUrl;
}