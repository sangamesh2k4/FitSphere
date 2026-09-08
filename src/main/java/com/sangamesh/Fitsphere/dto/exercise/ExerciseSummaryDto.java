package com.sangamesh.Fitsphere.dto.exercise;

import com.sangamesh.Fitsphere.enums.Category;
import com.sangamesh.Fitsphere.enums.Difficulty;
import com.sangamesh.Fitsphere.enums.Equipment;
import com.sangamesh.Fitsphere.enums.Muscle;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseSummaryDto {
    private Long id;

    private String name;

    private Category category;

    private Muscle primaryMuscle;

    private Equipment equipment;

    private Difficulty difficulty;

    private String imageUrl;

    private boolean active;
}
