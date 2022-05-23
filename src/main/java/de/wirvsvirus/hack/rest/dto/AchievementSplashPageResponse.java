package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.AchievementType;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AchievementSplashPageResponse {

  @NonNull
  AchievementType achievementType;

  // level achieved for a certain achievement
  int level;

  // visual representation
  @NonNull
  AchievementSplashPageType pageType;

  // not empty
  @NonNull
  String headline;

  // not empty
  @NonNull
  String bodyText;

  // not empty
  @NonNull
  String avatarUrl;

  @NonNull
  String avatarSvgUrl;

  // not empty - unicode text (e.g. heart or trophy)
  @NonNull
  String pageIcon;

  // 2+
  @NonNull
  List<RGBAColor> gradientColors;

  @NonNull
  RGBAColor ackButtonColor;

  // not empty
  @NonNull
  String ackButtonText;
}

