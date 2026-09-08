package com.sangamesh.Fitsphere.dto.exercise;

import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.enums.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDetailDto {

    private Long id;

    private String name;

    private Category category;

    private Muscle primaryMuscle;

    private List<Muscle> secondaryMuscles;

    private MovementPattern movementPattern;

    private Equipment equipment;

    private ExerciseType exerciseType;

    private Difficulty difficulty;

    private String description;

    private List<String> instructions;

    private List<String> tips;

    private List<String> commonMistakes;

    private String imageUrl;

    private List<YoutubeVideoDto> recommendedVideos;

    private Boolean active;

}