package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.AchievementType;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.RGBAColor;
import de.wirvsvirus.hack.service.achievement.SunshineHoursAchievement;
import de.wirvsvirus.hack.service.dto.AchievementSplashTextAndAvatarDto;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;
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

  // ATM we model only one visual type of achievement; to extend you need to use a more generic class
  public Optional<AchievementSplashTextAndAvatarDto> calculateCurrentSplash(
      final User currentUser) {
    final Group group = onboardingRepository.findGroupByUser(currentUser.getUserId())
        .orElseThrow(() -> new IllegalStateException("User not in any group"));

    // ATM only one achievement type is supported; more types requires a heuristic to pick one

    final Optional<AchievementSplashTextAndAvatarDto> a1 = calculateSunshineHours(
        currentUser, group);
    if (a1.isPresent()) {
      return a1;
    }

    final Optional<AchievementSplashTextAndAvatarDto> a2 = calculateSample();

    if (a2.isPresent()) {
      return a2;
    }

    return Optional.empty();
  }

  @Nonnull
  private Optional<AchievementSplashTextAndAvatarDto> calculateSunshineHours(
      User currentUser, Group group) {
    // sunshine hours
    final AchievementType achievementType = AchievementType.groupSunshineHours;
    Duration sunshine =
        statsService.calcSunshineTimeForGroup(group.getGroupId(), Instant.now());

    if (sunshine.isZero()) {
      System.out.println("dummy hours");
      sunshine = Duration.ofHours(2500);
    }

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

  @Nonnull
  private Optional<AchievementSplashTextAndAvatarDto> calculateSample() {
    return Optional.empty();
  }

  private AchievementSplashTextAndAvatarDto levelUpSunshine(final SunshineHoursAchievement achv) {
    final int level = achv.calcLevel();

    final StockAvatar stockAvatar;

    switch (level) {
      case 1:
        stockAvatar = StockAvatar.ICEBEAR;
        break;
      case 2:
        stockAvatar = StockAvatar.LEPRECHAUN;
        break;
      case 3:
        stockAvatar = StockAvatar.HIPSTER_SUMO_HAIR;
        break;
      case 4:
        stockAvatar = StockAvatar.GIRL_YELLOW;
        break;
      case 5:
        stockAvatar = StockAvatar.PIG_SHAMROCK;
        break;
      default:
        throw new IllegalStateException("no avatar for level " + level);
    }

    return AchievementSplashTextAndAvatarDto.builder()
        .achievementType(AchievementType.groupSunshineHours)
        .headline("You are a sunshine!")
        .bodyText(String.format("Eure Gruppe hat schon %d Sonnenstunden!", achv.getSunshineHours()))
        .stockAvatar(stockAvatar)
        .level(level)
        .gradientColors(Lists.newArrayList(
            RGBAColor.builder()
                .red(50)
                .green(99)
                .blue(200)
                .alpha(0.9f)
                .build(),
            RGBAColor.builder()
                .red(218)
                .green(140)
                .blue(200)
                .alpha(0.6f)
                .build()))
        .ackButtonColor(
            RGBAColor.builder()
                .red(20)
                .green(240)
                .blue(100)
                .alpha(0.6f)
                .build())
        .ackButtonText("Cool!")
        .build();
  }

  public void ackSplashSeen(final User currentUser, final AchievementType achievementType,
      final int level) {
    Preconditions.checkState(level > 0, "Cannot ack level %s", level);
    final int lastLevelUpShown = onboardingRepository.findLastLevelUpShown(currentUser.getUserId(),
        achievementType);
    log.info("User ack'd achievement splash for {} at level {}, prev level was {}", achievementType,
        level, lastLevelUpShown);
    Preconditions.checkState(level >= lastLevelUpShown, "Must not lower level");
    onboardingRepository.ackAchievementShowAtLevel(currentUser.getUserId(), achievementType, level);
  }

}
