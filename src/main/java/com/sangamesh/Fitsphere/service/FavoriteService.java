package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.entity.Favorite;

import java.util.List;

public interface FavoriteService {

    void addFavorite(Long exerciseId);

    void removeFavorite(Long exerciseId);

    List<ExerciseSummaryDto> getUserFavorites();

    boolean isFavorite(Long exerciseId);

    //food favorite services
    void addFoodFavorite(Long fdcId);

    void removeFoodFavorite(Long fdcId);

    List<FoodSummaryDto> getUserFoodFavorites();

    boolean isFoodFavorite(Long fdcId);
}