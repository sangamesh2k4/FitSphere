package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.config.ExerciseCategoryMuscleMapping;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseCategoryModulesDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseDetailDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseMetadataDto;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.ExerciseMapper;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;
import com.sangamesh.Fitsphere.service.ExerciseService;
import com.sangamesh.Fitsphere.service.YoutubeRecommendationService;
import com.sangamesh.Fitsphere.specification.ExerciseSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final YoutubeRecommendationService youtubeRecommendationService;
    private final ExerciseCategoryMuscleMapping exerciseCategoryMuscleMapping;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "name", "category", "primaryMuscle",
            "equipment", "difficulty", "exerciseType"
    );
    private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);


    @Value("${exercise.pagination.page-size}")
    private int pageSize;
    @Override
    public Page<ExerciseSummaryDto> getAllExercises(int page) {
        long totalExercises=exerciseRepository.countByActiveTrue();
        int totalPages=(int) Math.ceil((double) totalExercises/pageSize);

        if(page<0){
            page=0;
        }
        if(totalPages>0 && page>=totalPages){
            page=totalPages-1;
        }
        Pageable pageable= PageRequest.of(page, pageSize, Sort.by("name"));
        return exerciseRepository.findByActiveTrue(pageable).map(exerciseMapper::toSummaryDto);
    }

    @Override
    public ExerciseDetailDto getExerciseById(Long id) {
        Exercise exercise=exerciseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()->new ResourceNotFoundException("Exercise not found with id: "+id));
        ExerciseDetailDto dto=exerciseMapper.toDetailDto(exercise);
        try{
        dto.setRecommendedVideos(youtubeRecommendationService.searchTutorials(exercise.getName()));
        } catch (Exception e){
            log.error("Failed to fetch YouTube videos", e);
            dto.setRecommendedVideos(List.of());
        }
        return dto;
    }

    @Override
    public List<ExerciseSummaryDto> getExerciseByCategory(Category category) {

        return exerciseRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(exerciseMapper::toSummaryDto)
                .toList();
    }
    @Override
    public List<ExerciseSummaryDto> getExerciseByPrimaryMuscle(Muscle muscle) {

        return exerciseRepository.findByPrimaryMuscleAndActiveTrue(muscle)
                .stream()
                .map(exerciseMapper::toSummaryDto)
                .toList();
    }
    @Override
    public List<ExerciseSummaryDto> searchExercises(String keyword) {

        return exerciseRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword)
                .stream()
                .map(exerciseMapper::toSummaryDto)
                .toList();
    }


    @Override
    public Page<ExerciseSummaryDto> filterExercises(String keyword,Category category, Muscle primaryMuscle,
                                                    Equipment equipment,
                                                    Difficulty difficulty,
                                                    ExerciseType exerciseType,String sortBy,String direction,int page){

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "name";
        }
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            direction = "asc";
        }

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        page=Math.max(page,0);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Exercise> specification = Specification
                .where(ExerciseSpecification.isActive())
                .and(ExerciseSpecification.hasKeyword(keyword))
                .and(ExerciseSpecification.hasCategory(category))
                .and(ExerciseSpecification.hasPrimaryMuscle(primaryMuscle))
                .and(ExerciseSpecification.hasEquipment(equipment))
                .and(ExerciseSpecification.hasDifficulty(difficulty))
                .and(ExerciseSpecification.hasExerciseType(exerciseType));

        return exerciseRepository.findAll(specification,pageable).map(exerciseMapper::toSummaryDto);
    }


    @Override
    public ExerciseMetadataDto getExerciseMetadata() {

        return ExerciseMetadataDto.builder()
                .muscles(List.of(Muscle.values()))
                .equipment(List.of(Equipment.values()))
                .difficulties(List.of(Difficulty.values()))
                .categories(List.of(Category.values()))
                .exerciseTypes(List.of(ExerciseType.values()))
                .build();
    }
    @Override
    public ExerciseCategoryModulesDto getPrimaryMusclesByCategory(Category category) {
        List<Muscle> primaryMuscles = exerciseCategoryMuscleMapping.getPrimaryMuscles(category);

        return ExerciseCategoryModulesDto.builder()
                .category(category)
                .muscles(primaryMuscles)
                .build();
    }

}