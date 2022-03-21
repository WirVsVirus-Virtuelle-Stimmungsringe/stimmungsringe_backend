package de.wirvsvirus.hack.model;

import java.time.Instant;
import javax.annotation.Nonnegative;
import lombok.ToString;

@ToString
public class AchievementShownStatus implements MicrostreamObject {

  private AchievementType achievementType;
  private Instant shownAt;
  private int level;

  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
  }

  public Instant getShownAt() {
    return shownAt;
  }

  public void setShownAt(Instant shownAt) {
    this.shownAt = shownAt;
  }

  @Nonnegative
  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
