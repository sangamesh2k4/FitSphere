package com.sangamesh.Fitsphere.dto.exercise;

import com.sangamesh.Fitsphere.enums.Muscle;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseModuleDto {

    private Muscle primaryMuscle;
}