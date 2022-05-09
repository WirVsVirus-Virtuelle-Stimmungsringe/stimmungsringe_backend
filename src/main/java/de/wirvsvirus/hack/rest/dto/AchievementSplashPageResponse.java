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

    // level achieved for a certain achievement
    private int level;

    // not null - visual representation
    private AchievementSplashPageType pageType;

    // not empty
    private String headline;

    // not empty
    private String bodyText;

    // not empty
    private String avatarUrl;

    // not empty - unicode text (e.g. heart or trophy)
    private String pageIcon;

    // 2+
    private List<RGBAColor> gradientColors;

    private RGBAColor ackButtonColor;

    private String ackButtonText;

}

