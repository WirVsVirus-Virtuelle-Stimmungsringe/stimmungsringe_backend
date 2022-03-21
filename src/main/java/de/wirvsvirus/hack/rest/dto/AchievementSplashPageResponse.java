package de.wirvsvirus.hack.rest.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AchievementSplashPageResponse {

    // not null
    private AchievementSplashPageType pageType;

    // not empty
    private String headline;

    // not empty
    private String bodyText;

    // not empty
    private String avatarUrl;

}

