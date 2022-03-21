package de.wirvsvirus.hack.model;

import java.time.Instant;
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

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
