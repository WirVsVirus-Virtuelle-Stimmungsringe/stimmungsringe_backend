package de.wirvsvirus.hack.model;

import java.time.Instant;
import javax.annotation.Nonnegative;
import lombok.ToString;

@ToString
public class AchievementShownStatus implements MicrostreamObject {

  private Instant shownAt;
  private int level;

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
