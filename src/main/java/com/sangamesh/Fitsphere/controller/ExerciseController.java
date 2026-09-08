package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseCategoryModulesDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseMetadataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name="Exercise Library" ,description = "APIs for browsing ,searching and filtering exercises")
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Validated
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "Get all exercises")
    @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
    @GetMapping
    public Page<ExerciseSummaryDto> getAllExercises(@RequestParam(defaultValue = "0")int page) {
        return exerciseService.getAllExercises(page);
    }

    @Operation(summary = "Get exercise details", description = "Returns complete exercise information including instructions, tips, common mistakes and YouTube recommendations.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Exercise found"), @ApiResponse(responseCode = "404", description = "Exercise not found")})
    @GetMapping("/{id}")
    public ExerciseDetailDto getExerciseById(@PathVariable Long id) {
        return exerciseService.getExerciseById(id);
    }


    @GetMapping("/search")
    public List<ExerciseSummaryDto> searchExercises(
            @RequestParam String keyword) {

        return exerciseService.searchExercises(keyword);
    }

    @Operation(summary = "Filter exercises", description = "Search, filter, sort and paginate exercises.")
    @ApiResponse(responseCode = "200", description = "Exercises filtered successfully")
    @GetMapping("/filter")
    public Page<ExerciseSummaryDto> filterExercises(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Category category,
                                                    @RequestParam(required = false) Muscle primaryMuscle,
                                                    @RequestParam(required = false) Equipment equipment,
                                                    @RequestParam(required = false) Difficulty difficulty,
                                                    @RequestParam(required = false) ExerciseType exerciseType,
                                                    @RequestParam(defaultValue = "name") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction,
                                                    @RequestParam(defaultValue = "0") int page){
        return exerciseService.filterExercises(keyword,category, primaryMuscle, equipment, difficulty, exerciseType, sortBy,direction,page);
    }

    @GetMapping("/metadata")
    public ExerciseMetadataDto getMetadata() {
        return exerciseService.getExerciseMetadata();
    }

    @Operation(summary = "Get primary muscle options by category",
            description = "Returns the primary muscle options available for the selected exercise category.")
    @ApiResponse(responseCode = "200", description = "Primary muscle options retrieved successfully")
    @GetMapping("/category/{category}/primary-muscles")
    public ExerciseCategoryModulesDto getPrimaryMusclesByCategory(@PathVariable Category category) {
        return exerciseService.getPrimaryMusclesByCategory(category);
    }
}