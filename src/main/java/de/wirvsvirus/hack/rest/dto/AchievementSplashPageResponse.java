package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.AchievementType;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AchievementSplashPageResponse {

    // not null
    private AchievementType achievementType;

    private int level;

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

