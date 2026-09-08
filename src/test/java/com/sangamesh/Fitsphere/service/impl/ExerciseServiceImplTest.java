package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.config.ExerciseCategoryMuscleMapping;
import com.sangamesh.Fitsphere.dto.exercise.*;
import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.enums.*;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.ExerciseMapper;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;
import com.sangamesh.Fitsphere.service.YoutubeRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private YoutubeRecommendationService youtubeRecommendationService;

    @Mock
    private ExerciseCategoryMuscleMapping exerciseCategoryMuscleMapping;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                exerciseService,
                "pageSize",
                10
        );
    }

    @Test
    void getAllExercises_success() {

        Exercise exercise = new Exercise();

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .id(1L)
                        .name("Bench Press")
                        .build();

        Page<Exercise> page =
                new PageImpl<>(List.of(exercise));

        when(exerciseRepository.countByActiveTrue())
                .thenReturn(25L);

        when(exerciseRepository.findByActiveTrue(
                any(Pageable.class)))
                .thenReturn(page);

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        Page<ExerciseSummaryDto> result =
                exerciseService.getAllExercises(0);

        assertEquals(1, result.getContent().size());
        assertEquals("Bench Press",
                result.getContent().get(0).getName());

        verify(exerciseRepository)
                .countByActiveTrue();

        verify(exerciseRepository)
                .findByActiveTrue(any(Pageable.class));
    }

    @Test
    void getAllExercises_negativePage_defaultsToZero() {

        when(exerciseRepository.countByActiveTrue())
                .thenReturn(25L);

        when(exerciseRepository.findByActiveTrue(
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.getAllExercises(-5);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findByActiveTrue(captor.capture());

        assertEquals(0,
                captor.getValue().getPageNumber());
    }

    @Test
    void getAllExercises_pageBeyondLast_clampsToLastPage() {

        when(exerciseRepository.countByActiveTrue())
                .thenReturn(25L);

        when(exerciseRepository.findByActiveTrue(
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.getAllExercises(100);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findByActiveTrue(captor.capture());

        assertEquals(2,
                captor.getValue().getPageNumber());
    }

    @Test
    void getAllExercises_zeroExercises_doesNotClampPage() {

        when(exerciseRepository.countByActiveTrue())
                .thenReturn(0L);

        when(exerciseRepository.findByActiveTrue(
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.getAllExercises(5);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findByActiveTrue(captor.capture());

        assertEquals(5,
                captor.getValue().getPageNumber());
    }

    @Test
    void getExerciseById_successWithYoutubeVideos() {

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Bench Press");

        ExerciseDetailDto dto =
                new ExerciseDetailDto();

        List<YoutubeVideoDto> videos =
                List.of(new YoutubeVideoDto());

        when(exerciseRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(exercise));

        when(exerciseMapper.toDetailDto(exercise))
                .thenReturn(dto);

        when(youtubeRecommendationService.searchTutorials(
                "Bench Press"))
                .thenReturn(videos);

        ExerciseDetailDto result =
                exerciseService.getExerciseById(1L);

        assertSame(dto, result);
        assertEquals(videos, result.getRecommendedVideos());

        verify(exerciseMapper)
                .toDetailDto(exercise);

        verify(youtubeRecommendationService)
                .searchTutorials("Bench Press");
    }

    @Test
    void getExerciseById_throwsWhenNotFound() {

        when(exerciseRepository.findByIdAndActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> exerciseService.getExerciseById(99L)
        );

        verifyNoInteractions(
                exerciseMapper,
                youtubeRecommendationService
        );
    }

    @Test
    void getExerciseById_youtubeFailure_returnsEmptyVideos() {

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Squat");

        ExerciseDetailDto dto =
                new ExerciseDetailDto();

        when(exerciseRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(exercise));

        when(exerciseMapper.toDetailDto(exercise))
                .thenReturn(dto);

        when(youtubeRecommendationService.searchTutorials("Squat"))
                .thenThrow(new RuntimeException("YouTube failed"));

        ExerciseDetailDto result =
                exerciseService.getExerciseById(1L);

        assertNotNull(result.getRecommendedVideos());
        assertTrue(result.getRecommendedVideos().isEmpty());
    }

    @Test
    void getExerciseByCategory_success() {

        Exercise exercise = new Exercise();

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .name("Bench Press")
                        .build();

        when(exerciseRepository.findByCategoryAndActiveTrue(
                Category.CHEST))
                .thenReturn(List.of(exercise));

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        List<ExerciseSummaryDto> result =
                exerciseService.getExerciseByCategory(
                        Category.CHEST);

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void getExerciseByPrimaryMuscle_success() {

        Exercise exercise = new Exercise();

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .name("Incline Press")
                        .build();

        when(exerciseRepository.findByPrimaryMuscleAndActiveTrue(
                Muscle.UPPER_CHEST))
                .thenReturn(List.of(exercise));

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        List<ExerciseSummaryDto> result =
                exerciseService.getExerciseByPrimaryMuscle(
                        Muscle.UPPER_CHEST);

        assertEquals(1, result.size());
        assertEquals("Incline Press",
                result.get(0).getName());
    }

    @Test
    void searchExercises_success() {

        Exercise exercise = new Exercise();

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .name("Bench Press")
                        .build();

        when(exerciseRepository
                .findByNameContainingIgnoreCaseAndActiveTrue("bench"))
                .thenReturn(List.of(exercise));

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        List<ExerciseSummaryDto> result =
                exerciseService.searchExercises("bench");

        assertEquals(1, result.size());
        assertEquals("Bench Press",
                result.get(0).getName());
    }

    @Test
    void filterExercises_success() {

        Exercise exercise = new Exercise();

        ExerciseSummaryDto dto =
                ExerciseSummaryDto.builder()
                        .name("Bench Press")
                        .build();

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(exercise)));

        when(exerciseMapper.toSummaryDto(exercise))
                .thenReturn(dto);

        Page<ExerciseSummaryDto> result =
                exerciseService.filterExercises(
                        "bench",
                        Category.CHEST,
                        Muscle.UPPER_CHEST,
                        Equipment.BARBELL,
                        Difficulty.INTERMEDIATE,
                        ExerciseType.COMPOUND,
                        "name",
                        "asc",
                        0
                );

        assertEquals(1, result.getContent().size());
        assertEquals("Bench Press",
                result.getContent().get(0).getName());

        verify(exerciseRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void filterExercises_invalidSortField_defaultsToName() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.filterExercises(
                null,
                null,
                null,
                null,
                null,
                null,
                "invalidField",
                "asc",
                0
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Sort.Order order =
                captor.getValue()
                        .getSort()
                        .getOrderFor("name");

        assertNotNull(order);
        assertTrue(order.isAscending());
    }

    @Test
    void filterExercises_invalidDirection_defaultsToAsc() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.filterExercises(
                null,
                null,
                null,
                null,
                null,
                null,
                "name",
                "invalid",
                0
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Sort.Order order =
                captor.getValue()
                        .getSort()
                        .getOrderFor("name");

        assertNotNull(order);
        assertTrue(order.isAscending());
    }

    @Test
    void filterExercises_descendingSort() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.filterExercises(
                null,
                null,
                null,
                null,
                null,
                null,
                "difficulty",
                "desc",
                0
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Sort.Order order =
                captor.getValue()
                        .getSort()
                        .getOrderFor("difficulty");

        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void filterExercises_negativePage_defaultsToZero() {

        when(exerciseRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        exerciseService.filterExercises(
                null,
                null,
                null,
                null,
                null,
                null,
                "name",
                "asc",
                -10
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(exerciseRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        assertEquals(
                0,
                captor.getValue().getPageNumber()
        );
    }

    @Test
    void getExerciseMetadata_returnsAllMetadata() {

        ExerciseMetadataDto result =
                exerciseService.getExerciseMetadata();

        assertEquals(
                List.of(Muscle.values()),
                result.getMuscles()
        );

        assertEquals(
                List.of(Equipment.values()),
                result.getEquipment()
        );

        assertEquals(
                List.of(Difficulty.values()),
                result.getDifficulties()
        );

        assertEquals(
                List.of(Category.values()),
                result.getCategories()
        );

        assertEquals(
                List.of(ExerciseType.values()),
                result.getExerciseTypes()
        );
    }

    @Test
    void getPrimaryMusclesByCategory_success() {

        List<Muscle> muscles =
                List.of(
                        Muscle.UPPER_CHEST,
                        Muscle.MIDDLE_CHEST,
                        Muscle.LOWER_CHEST
                );

        when(exerciseCategoryMuscleMapping
                .getPrimaryMuscles(Category.CHEST))
                .thenReturn(muscles);

        ExerciseCategoryModulesDto result =
                exerciseService.getPrimaryMusclesByCategory(
                        Category.CHEST);

        assertEquals(Category.CHEST,
                result.getCategory());

        assertEquals(muscles,
                result.getMuscles());

        verify(exerciseCategoryMuscleMapping)
                .getPrimaryMuscles(Category.CHEST);
    }
}