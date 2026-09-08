package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.FavoriteType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {
        Favorite favorite = new Favorite();

        assertNull(favorite.getId());
        assertNull(favorite.getUser());
        assertNull(favorite.getType());
        assertNull(favorite.getExercise());
        assertNull(favorite.getFdcId());
        assertNull(favorite.getCreatedAt());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Long id = 1L;
        User user = new User();
        FavoriteType type = FavoriteType.EXERCISE;
        Exercise exercise = new Exercise();
        Long fdcId = 12345L;
        LocalDateTime createdAt = LocalDateTime.now();

        Favorite favorite = new Favorite(
                id,
                user,
                type,
                exercise,
                fdcId,
                createdAt
        );

        assertEquals(id, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(type, favorite.getType());
        assertEquals(exercise, favorite.getExercise());
        assertEquals(fdcId, favorite.getFdcId());
        assertEquals(createdAt, favorite.getCreatedAt());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Favorite favorite = new Favorite();

        User user = new User();
        Exercise exercise = new Exercise();
        LocalDateTime createdAt = LocalDateTime.now();

        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setType(FavoriteType.FOOD);
        favorite.setExercise(exercise);
        favorite.setFdcId(999L);
        favorite.setCreatedAt(createdAt);

        assertEquals(1L, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(FavoriteType.FOOD, favorite.getType());
        assertEquals(exercise, favorite.getExercise());
        assertEquals(999L, favorite.getFdcId());
        assertEquals(createdAt, favorite.getCreatedAt());
    }
}