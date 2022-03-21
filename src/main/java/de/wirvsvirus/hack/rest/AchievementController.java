package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.AchievementType;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.dto.AchievementSplashPageResponse;
import de.wirvsvirus.hack.rest.dto.AchievementSplashPageType;
import de.wirvsvirus.hack.rest.dto.AcknowledgeAchievementSplashRequest;
import de.wirvsvirus.hack.service.AchievementService;
import de.wirvsvirus.hack.service.dto.AchievementSplashTextAndAvatarDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/achievement")
@Slf4j
public class AchievementController {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private AchievementService achievementService;

  @Autowired
  private AvatarUrlResolver avatarUrlResolver;

  @GetMapping("/splash/{achievementType}")
  public ResponseEntity<AchievementSplashPageResponse> getCurrentSplash(
      @NotNull @PathVariable("achievementType") final AchievementType achievementType) {
    final User currentUser = onboardingRepository.lookupUserById(UserInterceptor.getCurrentUserId());

    final Optional<AchievementSplashTextAndAvatarDto> splashDtoOpt =
        achievementService.calculateCurrentSplash(currentUser);

    if (!splashDtoOpt.isPresent()) {
      return ResponseEntity.noContent().build();
    }
    final AchievementSplashTextAndAvatarDto splashDto = splashDtoOpt.get();

    return
        ResponseEntity.ok(
          AchievementSplashPageResponse.builder()
            .pageType(AchievementSplashPageType.AVATAR_WITH_TEXT)
            .headline(splashDto.getHeadline())
            .bodyText(splashDto.getBodyText())
            .avatarUrl(avatarUrlResolver.getStockAvatarUrl(splashDto.getStockAvatar()))
            .build());
  }

  @PostMapping
  public void removeSplashForLevel(
      @NotNull @RequestBody AcknowledgeAchievementSplashRequest request,
      @NotNull @PathVariable("achievementType") final AchievementType achievementType,
      final int levelSeen) {

    achievementService.ackSplashSeen(achievementType, request.getLevel());

  }

}
