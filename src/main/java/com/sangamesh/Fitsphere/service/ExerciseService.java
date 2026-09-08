package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseCategoryModulesDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseMetadataDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ExerciseService {

    Page<ExerciseSummaryDto> getAllExercises(int page);

    ExerciseDetailDto getExerciseById(Long id);
    ExerciseCategoryModulesDto getPrimaryMusclesByCategory(Category category);

    List<ExerciseSummaryDto> getExerciseByCategory(Category category);

    List<ExerciseSummaryDto> getExerciseByPrimaryMuscle(Muscle muscle);

    List<ExerciseSummaryDto> searchExercises(String Keyword);

    Page<ExerciseSummaryDto> filterExercises(String keyword,Category category, Muscle primaryMuscle,
                                             Equipment equipment, Difficulty difficulty,
                                             ExerciseType exerciseType,   String sortBy,
                                             String direction, int page);

    ExerciseMetadataDto getExerciseMetadata();
}
