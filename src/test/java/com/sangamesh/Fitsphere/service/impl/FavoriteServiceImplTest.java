package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.Favorite;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Category;
import com.sangamesh.Fitsphere.enums.Difficulty;
import com.sangamesh.Fitsphere.enums.Equipment;
import com.sangamesh.Fitsphere.enums.FavoriteType;
import com.sangamesh.Fitsphere.enums.Muscle;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.NutritionMapper;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;
import com.sangamesh.Fitsphere.repository.FavoriteRepository;
import com.sangamesh.Fitsphere.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private USDAClient usdaClient;

    @Mock
    private NutritionMapper nutritionMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");

        exercise = new Exercise();
        exercise.setId(10L);
        exercise.setName("Bench Press");
        exercise.setCategory(Category.CHEST);
        exercise.setPrimaryMuscle(Muscle.MIDDLE_CHEST);
        exercise.setEquipment(Equipment.BARBELL);
        exercise.setDifficulty(Difficulty.INTERMEDIATE);
        exercise.setImageUrl("bench.jpg");
        exercise.setActive(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "john",
                        null
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------
    // Exercise favorites
    // ---------------------------------------------------------

    @Test
    void addFavorite_success() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.existsByUserAndExercise(user, exercise))
                .thenReturn(false);

        favoriteService.addFavorite(10L);

        verify(favoriteRepository).save(argThat(favorite ->
                favorite.getUser() == user &&
                        favorite.getExercise() == exercise &&
                        favorite.getType() == FavoriteType.EXERCISE &&
                        favorite.getCreatedAt() != null
        ));
    }

    @Test
    void addFavorite_throwsWhenExerciseDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.addFavorite(10L)
        );

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_throwsWhenAlreadyFavorite() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.existsByUserAndExercise(user, exercise))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> favoriteService.addFavorite(10L)
        );

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void removeFavorite_success() {

        Favorite favorite = Favorite.builder()
                .id(100L)
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.findByUserAndExercise(user, exercise))
                .thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(10L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void removeFavorite_throwsWhenExerciseDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.removeFavorite(10L)
        );

        verify(favoriteRepository, never()).delete(any());
    }

    @Test
    void removeFavorite_throwsWhenFavoriteDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.findByUserAndExercise(user, exercise))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.removeFavorite(10L)
        );

        verify(favoriteRepository, never()).delete(any());
    }

    @Test
    void getUserFavorites_returnsMappedExercises() {

        Favorite favorite = Favorite.builder()
                .id(100L)
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndType(user, FavoriteType.EXERCISE))
                .thenReturn(List.of(favorite));

        List<ExerciseSummaryDto> result =
                favoriteService.getUserFavorites();

        assertEquals(1, result.size());

        ExerciseSummaryDto dto = result.get(0);

        assertEquals(10L, dto.getId());
        assertEquals("Bench Press", dto.getName());
        assertEquals(Category.CHEST, dto.getCategory());
        assertEquals(Muscle.MIDDLE_CHEST, dto.getPrimaryMuscle());
        assertEquals(Equipment.BARBELL, dto.getEquipment());
        assertEquals(Difficulty.INTERMEDIATE, dto.getDifficulty());
        assertEquals("bench.jpg", dto.getImageUrl());
        assertTrue(dto.isActive());
    }

    @Test
    void getUserFavorites_returnsEmptyList() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndType(user, FavoriteType.EXERCISE))
                .thenReturn(List.of());

        List<ExerciseSummaryDto> result =
                favoriteService.getUserFavorites();

        assertTrue(result.isEmpty());
    }

    @Test
    void isFavorite_returnsTrue() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.existsByUserAndExercise(user, exercise))
                .thenReturn(true);

        assertTrue(favoriteService.isFavorite(10L));
    }

    @Test
    void isFavorite_returnsFalse() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.of(exercise));

        when(favoriteRepository.existsByUserAndExercise(user, exercise))
                .thenReturn(false);

        assertFalse(favoriteService.isFavorite(10L));
    }

    @Test
    void isFavorite_throwsWhenExerciseDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(exerciseRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.isFavorite(10L)
        );
    }

    // ---------------------------------------------------------
    // Food favorites
    // ---------------------------------------------------------

    @Test
    void addFoodFavorite_success() {

        USDAFoodDto food = new USDAFoodDto();
        food.setFdcId(12345L);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.existsByUserAndFdcId(user, 12345L))
                .thenReturn(false);

        when(usdaClient.getFoodDetails(12345L))
                .thenReturn(food);

        favoriteService.addFoodFavorite(12345L);

        verify(favoriteRepository).save(argThat(favorite ->
                favorite.getUser() == user &&
                        favorite.getType() == FavoriteType.FOOD &&
                        favorite.getFdcId().equals(12345L) &&
                        favorite.getCreatedAt() != null
        ));
    }

    @Test
    void addFoodFavorite_throwsWhenAlreadyFavorite() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.existsByUserAndFdcId(user, 12345L))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> favoriteService.addFoodFavorite(12345L)
        );

        verify(usdaClient, never()).getFoodDetails(anyLong());
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void getUserFoodFavorites_returnsMappedFoods() {

        Favorite favorite = Favorite.builder()
                .id(200L)
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .build();

        USDAFoodDto food = new USDAFoodDto();
        food.setFdcId(12345L);
        food.setDescription("Chicken Breast");

        FoodSummaryDto summary = new FoodSummaryDto();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndType(user, FavoriteType.FOOD))
                .thenReturn(List.of(favorite));

        when(usdaClient.getFoodDetails(12345L))
                .thenReturn(food);

        when(nutritionMapper.toFoodSummaryDto(food))
                .thenReturn(summary);

        List<FoodSummaryDto> result =
                favoriteService.getUserFoodFavorites();

        assertEquals(1, result.size());
        assertSame(summary, result.get(0));

        verify(usdaClient).getFoodDetails(12345L);
        verify(nutritionMapper).toFoodSummaryDto(food);
    }

    @Test
    void getUserFoodFavorites_returnsEmptyList() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndType(user, FavoriteType.FOOD))
                .thenReturn(List.of());

        List<FoodSummaryDto> result =
                favoriteService.getUserFoodFavorites();

        assertTrue(result.isEmpty());

        verifyNoInteractions(usdaClient, nutritionMapper);
    }

    @Test
    void isFoodFavorite_returnsTrue() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.existsByUserAndFdcId(user, 12345L))
                .thenReturn(true);

        assertTrue(favoriteService.isFoodFavorite(12345L));
    }

    @Test
    void isFoodFavorite_returnsFalse() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.existsByUserAndFdcId(user, 12345L))
                .thenReturn(false);

        assertFalse(favoriteService.isFoodFavorite(12345L));
    }

    @Test
    void removeFoodFavorite_success() {

        Favorite favorite = Favorite.builder()
                .id(200L)
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndFdcId(user, 12345L))
                .thenReturn(Optional.of(favorite));

        favoriteService.removeFoodFavorite(12345L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void removeFoodFavorite_throwsWhenFavoriteDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(favoriteRepository.findByUserAndFdcId(user, 12345L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.removeFoodFavorite(12345L)
        );

        verify(favoriteRepository, never()).delete(any());
    }

    // ---------------------------------------------------------
    // Current user
    // ---------------------------------------------------------

    @Test
    void throwsWhenCurrentUserDoesNotExist() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favoriteService.isFoodFavorite(12345L)
        );
    }
}