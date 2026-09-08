package com.sangamesh.Fitsphere.dto.workout;


import lombok.Data;

import java.util.List;

@Data
public class ReorderWorkoutExercisesRequestDto {

    private List<Long> exerciseIds;
}
