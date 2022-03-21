package de.wirvsvirus.hack.rest.dto;

import java.time.Instant;
import java.util.List;
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

    // 2+
    private List<RGBAColor> gradientColors;

}

