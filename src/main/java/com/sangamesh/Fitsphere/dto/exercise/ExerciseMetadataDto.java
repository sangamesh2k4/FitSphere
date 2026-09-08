package com.sangamesh.Fitsphere.dto.exercise;

import com.sangamesh.Fitsphere.enums.*;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseMetadataDto {

    private List<Muscle> muscles;

    private List<Equipment> equipment;

    private List<Difficulty> difficulties;

    private List<Category> categories;

    private List<ExerciseType> exerciseTypes;
}