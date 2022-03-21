package de.wirvsvirus.hack.service.dto;

import de.wirvsvirus.hack.model.StockAvatar;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder
@Data
public class AchievementSplashTextAndAvatarDto {

  private String headline;
  private String bodyText;
  private StockAvatar stockAvatar;

}