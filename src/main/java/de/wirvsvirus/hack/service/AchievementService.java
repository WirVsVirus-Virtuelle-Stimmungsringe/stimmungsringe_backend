package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.AchievementType;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.service.achievement.SunshineHoursAchievement;
import de.wirvsvirus.hack.service.dto.AchievementSplashTextAndAvatarDto;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AchievementService {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private StatsService statsService;

  public Optional<AchievementSplashTextAndAvatarDto> calculateCurrentSplash(final User currentUser) {
    final Group group = onboardingRepository.findGroupByUser(currentUser.getUserId())
        .orElseThrow(() -> new IllegalStateException("User not in any group"));

    Duration sunshine =
        statsService.calcSunshineTimeForGroup(group.getGroupId(), Instant.now());

    if (sunshine.isZero()) {
      System.out.println("dummy hours");
      sunshine = Duration.ofHours(2500);
    }

    final AchievementType achievementType = AchievementType.GROUP_SUNSHINE_HOURS;

    final int lastLevelUpShown = onboardingRepository.findLastLevelUpShown(
        currentUser.getUserId(), achievementType);

    final SunshineHoursAchievement a1 = new SunshineHoursAchievement(sunshine);
    if (a1.isLevelUp(lastLevelUpShown)) {
      log.info("Show level-up for achievement {} to user {}", achievementType, currentUser);

      // select achievement level-up as best and return it
      return Optional.of(levelUpSunshine(a1));
    }

    return Optional.empty();
  }

  private AchievementSplashTextAndAvatarDto levelUpSunshine(final SunshineHoursAchievement achv) {
    final int level = achv.calcLevel();

    final StockAvatar stockAvatar;

    switch (level) {
      case 1: stockAvatar = StockAvatar.ICEBEAR;
        break;
      case 2: stockAvatar = StockAvatar.LEPRECHAUN;
        break;
      case 3: stockAvatar = StockAvatar.HIPSTER_SUMO_HAIR;
        break;
      case 4: stockAvatar = StockAvatar.GIRL_YELLOW;
        break;
      case 5: stockAvatar = StockAvatar.PIG_SHAMROCK;
        break;
      default:
        throw new IllegalStateException("no avatar for level " + level);
    }

    return AchievementSplashTextAndAvatarDto.builder()
        .headline("You are a sunsine!")
        .bodyText(String.format("Eure Gruppe hat schon %d Sonnenstunden!", achv.getSunshineHours()))
        .stockAvatar(stockAvatar)
        .build();
  }

  public void ackSplashSeen(User currentUser, AchievementType achievementType, int level) {
    Preconditions.checkState(level > 0, "Cannot ack level %s", level);
    final int lastLevelUpShown = onboardingRepository.findLastLevelUpShown(currentUser.getUserId(),
        achievementType);
    log.info("User ack'd achievement splash for {} at level {}, prev level was {}", achievementType, level, lastLevelUpShown);
    Preconditions.checkState(level >= lastLevelUpShown, "Must not lower level");
    onboardingRepository.ackAchievementShowAtLevel(currentUser.getUserId(), achievementType, level);
  }
}
