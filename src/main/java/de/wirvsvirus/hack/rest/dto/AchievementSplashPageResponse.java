package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.AchievementType;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AchievementSplashPageResponse {

  // not null
  AchievementType achievementType;

  // level achieved for a certain achievement
  int level;

  // not null - visual representation
  AchievementSplashPageType pageType;

  // not empty
  String headline;

  // not empty
  String bodyText;

  // not empty
  String avatarUrl;

  // nullable
  String avatarSvgUrl;

  // not empty - unicode text (e.g. heart or trophy)
  String pageIcon;

  // 2+
  List<RGBAColor> gradientColors;

  RGBAColor ackButtonColor;

  String ackButtonText;
}

