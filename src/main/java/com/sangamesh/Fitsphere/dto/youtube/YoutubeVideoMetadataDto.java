package com.sangamesh.Fitsphere.dto.youtube;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YoutubeVideoMetadataDto {

    private String videoId;

    private String title;

    private String channelTitle;

    private String thumbnailUrl;

    private String duration;
}