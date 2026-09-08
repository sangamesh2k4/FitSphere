package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{exerciseId}")
    public String addFavorite(@PathVariable Long exerciseId) {
        favoriteService.addFavorite(exerciseId);
        return "Exercise added to favorites.";
    }

    @DeleteMapping("/{exerciseId}")
    public String removeFavorite(@PathVariable Long exerciseId) {
        favoriteService.removeFavorite(exerciseId);
        return "Exercise removed from favorites.";
    }

    @GetMapping
    public List<ExerciseSummaryDto> getFavorites() {
        return favoriteService.getUserFavorites();
    }

    @GetMapping("/{exerciseId}/status")
    public boolean isFavorite(@PathVariable Long exerciseId) {
        return favoriteService.isFavorite(exerciseId);
    }

    @PostMapping("/foods/{fdcId}")
    public String addFoodFavorite(@PathVariable Long fdcId) {
        favoriteService.addFoodFavorite(fdcId);
        return "Food added to favorites.";
    }

    @DeleteMapping("/foods/{fdcId}")
    public String removeFoodFavorite(@PathVariable Long fdcId) {
        favoriteService.removeFoodFavorite(fdcId);
        return "Food removed from favorites.";
    }

    @GetMapping("/foods")
    public List<FoodSummaryDto> getFoodFavorites() {
        return favoriteService.getUserFoodFavorites();
    }

    @GetMapping("/foods/{fdcId}/status")
    public boolean isFoodFavorite(@PathVariable Long fdcId) {
        return favoriteService.isFoodFavorite(fdcId);
    }
}