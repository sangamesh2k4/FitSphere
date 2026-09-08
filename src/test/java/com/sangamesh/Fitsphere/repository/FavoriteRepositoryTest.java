package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.Favorite;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private User user;
    private Exercise exercise;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setEmailVerified(true);

        user = userRepository.save(user);

        exercise = new Exercise();
        exercise.setName("Bench Press");
        exercise.setCategory(Category.CHEST);
        exercise.setPrimaryMuscle(Muscle.MIDDLE_CHEST);
        exercise.setEquipment(Equipment.BARBELL);
        exercise.setMovementPattern(MovementPattern.HORIZONTAL_PUSH);
        exercise.setDifficulty(Difficulty.INTERMEDIATE);
        exercise.setExerciseType(ExerciseType.COMPOUND);
        exercise.setImageUrl("bench.jpg");
        exercise.setActive(true);

        exercise = exerciseRepository.save(exercise);
    }

    // ---------------------------------------------------------
    // Exercise favorites
    // ---------------------------------------------------------

    @Test
    void existsByUserAndExercise_returnsTrue() {

        Favorite favorite = Favorite.builder()
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        assertTrue(
                favoriteRepository.existsByUserAndExercise(user, exercise)
        );
    }

    @Test
    void existsByUserAndExercise_returnsFalse() {

        assertFalse(
                favoriteRepository.existsByUserAndExercise(user, exercise)
        );
    }

    @Test
    void findByUserAndExercise_returnsFavorite() {

        Favorite favorite = Favorite.builder()
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        Optional<Favorite> result =
                favoriteRepository.findByUserAndExercise(user, exercise);

        assertTrue(result.isPresent());
        assertEquals(favorite.getId(), result.get().getId());
    }

    @Test
    void findByUserAndExercise_returnsEmptyWhenNotFound() {

        Optional<Favorite> result =
                favoriteRepository.findByUserAndExercise(user, exercise);

        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // Food favorites
    // ---------------------------------------------------------

    @Test
    void existsByUserAndFdcId_returnsTrue() {

        Favorite favorite = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        assertTrue(
                favoriteRepository.existsByUserAndFdcId(user, 12345L)
        );
    }

    @Test
    void existsByUserAndFdcId_returnsFalse() {

        assertFalse(
                favoriteRepository.existsByUserAndFdcId(user, 12345L)
        );
    }

    @Test
    void findByUserAndFdcId_returnsFavorite() {

        Favorite favorite = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        Optional<Favorite> result =
                favoriteRepository.findByUserAndFdcId(user, 12345L);

        assertTrue(result.isPresent());
        assertEquals(favorite.getId(), result.get().getId());
        assertEquals(12345L, result.get().getFdcId());
    }

    @Test
    void findByUserAndFdcId_returnsEmptyWhenNotFound() {

        Optional<Favorite> result =
                favoriteRepository.findByUserAndFdcId(user, 12345L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserAndFdcIdIsNotNull_returnsFoodFavorites() {

        Favorite food1 = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .createdAt(LocalDateTime.now())
                .build();

        Favorite food2 = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(67890L)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(List.of(food1, food2));

        List<Favorite> result =
                favoriteRepository.findByUserAndFdcIdIsNotNull(user);

        assertEquals(2, result.size());
        assertTrue(
                result.stream().allMatch(f -> f.getFdcId() != null)
        );
    }

    // ---------------------------------------------------------
    // Type filtering
    // ---------------------------------------------------------

    @Test
    void findByUserAndType_returnsExerciseFavorites() {

        Favorite exerciseFavorite = Favorite.builder()
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .createdAt(LocalDateTime.now())
                .build();

        Favorite foodFavorite = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(
                List.of(exerciseFavorite, foodFavorite)
        );

        List<Favorite> result =
                favoriteRepository.findByUserAndType(
                        user,
                        FavoriteType.EXERCISE
                );

        assertEquals(1, result.size());
        assertEquals(
                FavoriteType.EXERCISE,
                result.get(0).getType()
        );
    }

    @Test
    void findByUserAndType_returnsFoodFavorites() {

        Favorite exerciseFavorite = Favorite.builder()
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .createdAt(LocalDateTime.now())
                .build();

        Favorite foodFavorite = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(12345L)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(
                List.of(exerciseFavorite, foodFavorite)
        );

        List<Favorite> result =
                favoriteRepository.findByUserAndType(
                        user,
                        FavoriteType.FOOD
                );

        assertEquals(1, result.size());
        assertEquals(
                FavoriteType.FOOD,
                result.get(0).getType()
        );
        assertEquals(12345L, result.get(0).getFdcId());
    }
}