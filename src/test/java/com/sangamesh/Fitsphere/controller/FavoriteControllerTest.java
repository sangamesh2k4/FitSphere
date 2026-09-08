package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.exercise.ExerciseSummaryDto;
import com.sangamesh.Fitsphere.dto.nutrition.FoodSummaryDto;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void addFavorite_success() throws Exception {
        doNothing().when(favoriteService).addFavorite(1L);

        mockMvc.perform(post("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Exercise added to favorites."));

        verify(favoriteService).addFavorite(1L);
    }

    @Test
    void removeFavorite_success() throws Exception {
        doNothing().when(favoriteService).removeFavorite(1L);

        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Exercise removed from favorites."));

        verify(favoriteService).removeFavorite(1L);
    }

    @Test
    void getFavorites_success() throws Exception {
        ExerciseSummaryDto dto = ExerciseSummaryDto.builder()
                .id(1L)
                .name("Bench Press")
                .build();

        when(favoriteService.getUserFavorites())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Bench Press"));

        verify(favoriteService).getUserFavorites();
    }

    @Test
    void isFavorite_success() throws Exception {
        when(favoriteService.isFavorite(1L)).thenReturn(true);

        mockMvc.perform(get("/api/favorites/1/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(favoriteService).isFavorite(1L);
    }

    @Test
    void addFoodFavorite_success() throws Exception {
        doNothing().when(favoriteService).addFoodFavorite(100L);

        mockMvc.perform(post("/api/favorites/foods/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Food added to favorites."));

        verify(favoriteService).addFoodFavorite(100L);
    }

    @Test
    void removeFoodFavorite_success() throws Exception {
        doNothing().when(favoriteService).removeFoodFavorite(100L);

        mockMvc.perform(delete("/api/favorites/foods/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Food removed from favorites."));

        verify(favoriteService).removeFoodFavorite(100L);
    }

    @Test
    void getFoodFavorites_success() throws Exception {
        FoodSummaryDto dto = new FoodSummaryDto();

        when(favoriteService.getUserFoodFavorites())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/favorites/foods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(favoriteService).getUserFoodFavorites();
    }

    @Test
    void isFoodFavorite_success() throws Exception {
        when(favoriteService.isFoodFavorite(100L)).thenReturn(true);

        mockMvc.perform(get("/api/favorites/foods/100/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(favoriteService).isFoodFavorite(100L);
    }

    @Test
    void invalidExerciseId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/favorites/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidFoodId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/favorites/foods/abc"))
                .andExpect(status().isBadRequest());
    }
}