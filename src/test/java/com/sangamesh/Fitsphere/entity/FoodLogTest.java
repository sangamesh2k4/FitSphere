package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.MealType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FoodLogTest {

    @Test
    void defaultConstructor_createsEmptyFoodLog() {
        FoodLog foodLog = new FoodLog();

        assertNull(foodLog.getId());
        assertNull(foodLog.getUser());
        assertNull(foodLog.getFdcId());
        assertNull(foodLog.getFoodName());
        assertNull(foodLog.getQuantity());
        assertNull(foodLog.getUnit());
        assertNull(foodLog.getMealType());
        assertNull(foodLog.getCalories());
        assertNull(foodLog.getProtein());
        assertNull(foodLog.getCarbohydrates());
        assertNull(foodLog.getFat());
        assertNull(foodLog.getFiber());
        assertNull(foodLog.getSugar());
        assertNull(foodLog.getSodium());
        assertNull(foodLog.getLogDate());
        assertNull(foodLog.getLoggedAt());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        FoodLog foodLog = new FoodLog();

        User user = new User();
        LocalDate date = LocalDate.of(2026, 9, 7);
        LocalDateTime loggedAt = LocalDateTime.of(2026, 9, 7, 8, 30);

        foodLog.setId(1L);
        foodLog.setUser(user);
        foodLog.setFdcId(12345L);
        foodLog.setFoodName("Chicken Breast");
        foodLog.setQuantity(150.0);
        foodLog.setUnit("g");
        foodLog.setMealType(MealType.LUNCH);
        foodLog.setCalories(248.0);
        foodLog.setProtein(46.5);
        foodLog.setCarbohydrates(0.0);
        foodLog.setFat(5.4);
        foodLog.setFiber(0.0);
        foodLog.setSugar(0.0);
        foodLog.setSodium(111.0);
        foodLog.setLogDate(date);
        foodLog.setLoggedAt(loggedAt);

        assertEquals(1L, foodLog.getId());
        assertSame(user, foodLog.getUser());
        assertEquals(12345L, foodLog.getFdcId());
        assertEquals("Chicken Breast", foodLog.getFoodName());
        assertEquals(150.0, foodLog.getQuantity());
        assertEquals("g", foodLog.getUnit());
        assertEquals(MealType.LUNCH, foodLog.getMealType());
        assertEquals(248.0, foodLog.getCalories());
        assertEquals(46.5, foodLog.getProtein());
        assertEquals(0.0, foodLog.getCarbohydrates());
        assertEquals(5.4, foodLog.getFat());
        assertEquals(0.0, foodLog.getFiber());
        assertEquals(0.0, foodLog.getSugar());
        assertEquals(111.0, foodLog.getSodium());
        assertEquals(date, foodLog.getLogDate());
        assertEquals(loggedAt, foodLog.getLoggedAt());
    }

    @Test
    void setters_canUpdateExistingValues() {
        FoodLog foodLog = new FoodLog();

        foodLog.setFoodName("Rice");
        foodLog.setQuantity(100.0);
        foodLog.setMealType(MealType.DINNER);

        assertEquals("Rice", foodLog.getFoodName());
        assertEquals(100.0, foodLog.getQuantity());
        assertEquals(MealType.DINNER, foodLog.getMealType());

        foodLog.setFoodName("Oats");
        foodLog.setQuantity(80.0);
        foodLog.setMealType(MealType.BREAKFAST);

        assertEquals("Oats", foodLog.getFoodName());
        assertEquals(80.0, foodLog.getQuantity());
        assertEquals(MealType.BREAKFAST, foodLog.getMealType());
    }
}