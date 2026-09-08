package com.sangamesh.Fitsphere.dto.exercise;

import com.sangamesh.Fitsphere.enums.Category;
import com.sangamesh.Fitsphere.enums.Muscle;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseCategoryModulesDto {

    private Category category;

    private List<Muscle> muscles;
}