package com.sangamesh.Fitsphere.dto.youtube;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeVideoDto {

    private String videoId;

    private String title;

    private String channelTitle;

    private String thumbnailUrl;

    private String videoUrl;
}