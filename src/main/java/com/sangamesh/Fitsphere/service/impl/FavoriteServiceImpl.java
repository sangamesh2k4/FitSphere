package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.client.USDAClient;
import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.Favorite;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.FavoriteType;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.NutritionMapper;
import com.sangamesh.Fitsphere.repository.FavoriteRepository;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.repository.ExerciseRepository;

import com.sangamesh.Fitsphere.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final USDAClient usdaClient;
    private final NutritionMapper nutritionMapper;

    @Override
    public void addFavorite(Long exerciseId) {
        User user = getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                        new ResourceNotFoundException("Exercise not found"));

        if (favoriteRepository.existsByUserAndExercise(user, exercise)) {
            throw new BadRequestException("Exercise already in favorites.");
        }
        Favorite favorite = Favorite.builder()
                .user(user)
                .exercise(exercise)
                .type(FavoriteType.EXERCISE)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(Long exerciseId) {
        User user = getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                        new ResourceNotFoundException("Exercise not found"));
        Favorite favorite = favoriteRepository.findByUserAndExercise(user, exercise)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
    @Override
    public List<ExerciseSummaryDto> getUserFavorites() {

        User user = getCurrentUser();

        return favoriteRepository.findByUserAndType(user,FavoriteType.EXERCISE)
                .stream()
                .map(favorite -> {
                    Exercise exercise = favorite.getExercise();

                    return ExerciseSummaryDto.builder()
                            .id(exercise.getId())
                            .name(exercise.getName())
                            .category(exercise.getCategory())
                            .primaryMuscle(exercise.getPrimaryMuscle())
                            .equipment(exercise.getEquipment())
                            .difficulty(exercise.getDifficulty())
                            .imageUrl(exercise.getImageUrl())
                            .active(exercise.getActive())
                            .build();
                })
                .toList();
    }

    @Override
    public boolean isFavorite(Long exerciseId) {
        User user = getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() ->
                new ResourceNotFoundException("Exercise not found"));
        return favoriteRepository.existsByUserAndExercise(user, exercise);
    }

    //food favorite services
    @Override
    public void addFoodFavorite(Long fdcId) {

        User user = getCurrentUser();

        if (favoriteRepository.existsByUserAndFdcId(user, fdcId)) {
            throw new BadRequestException("Food already in favorites.");
        }

        USDAFoodDto food = usdaClient.getFoodDetails(fdcId);

        Favorite favorite = Favorite.builder()
                .user(user)
                .type(FavoriteType.FOOD)
                .fdcId(food.getFdcId())
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    public List<FoodSummaryDto> getUserFoodFavorites() {
        User user = getCurrentUser();
        return favoriteRepository.findByUserAndType(user, FavoriteType.FOOD)
                .stream()
                .map(favorite -> {
                    USDAFoodDto food = usdaClient.getFoodDetails(favorite.getFdcId());
                    return nutritionMapper.toFoodSummaryDto(food);
                })
                .toList();
    }
    @Override
    public boolean isFoodFavorite(Long fdcId) {
        User user = getCurrentUser();
        return favoriteRepository.existsByUserAndFdcId(user, fdcId);
    }
    @Override
    public void removeFoodFavorite(Long fdcId) {
        User user = getCurrentUser();
        Favorite favorite = favoriteRepository.findByUserAndFdcId(user, fdcId)
                .orElseThrow(() -> new ResourceNotFoundException("Food favorite not found"));
        favoriteRepository.delete(favorite);
    }







    //helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }
}
