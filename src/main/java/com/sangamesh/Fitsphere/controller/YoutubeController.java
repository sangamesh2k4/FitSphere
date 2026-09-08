package com.sangamesh.Fitsphere.controller;
import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoMetadataDto;
import com.sangamesh.Fitsphere.service.YoutubeRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {


        private final YoutubeRecommendationService youtubeRecommendationService;

        @GetMapping("/search")
        public List<YoutubeVideoDto> search(
                @RequestParam String exercise) {

            return youtubeRecommendationService.searchTutorials(exercise);
        }
    }
