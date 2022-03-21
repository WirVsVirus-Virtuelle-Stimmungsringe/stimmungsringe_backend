package de.wirvsvirus.hack.service.achievement;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.AchievementType;
import java.time.Duration;
import javax.naming.ldap.PagedResultsControl;
import lombok.Value;

@Value
public class SunshineHoursAchievement {

  Duration sunshineDuration;
  AchievementType achievementType = AchievementType.GROUP_SUNSHINE_HOURS;

  public int getSunshineHours() {
    return (int) sunshineDuration.toHours();
  }

  public boolean isLevelUp(final int lastLevel) {
    Preconditions.checkState(lastLevel >= 0);
    final int calcLevel = calcLevel();
    if (calcLevel == 0) {
      return false;
    }
    return calcLevel > lastLevel;
  }

  // 0..5
  public int calcLevel() {
    final long hours = sunshineDuration.toHours();
    if (hours < 100) {
      return 0;
    } else if (hours < 200) {
      return 1;
    } else if (hours < 500) {
      return 2;
    } else if (hours < 1000) {
      return 3;
    } else if (hours < 2000) {
      return 4;
    } else {
      return 5;
    }
  }
}
