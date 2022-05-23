package de.wirvsvirus.hack.rest;

import de.wirvsvirus.hack.model.AchievementType;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.rest.AvatarUrlResolver.AvatarUrls;
import de.wirvsvirus.hack.rest.dto.AchievementSplashPageResponse;
import de.wirvsvirus.hack.rest.dto.AchievementSplashPageType;
import de.wirvsvirus.hack.rest.dto.AcknowledgeAchievementSplashRequest;
import de.wirvsvirus.hack.service.AchievementService;
import de.wirvsvirus.hack.service.dto.AchievementSplashTextAndAvatarDto;
import de.wirvsvirus.hack.spring.UserInterceptor;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/achievement")
@Slf4j
public class AchievementController {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private AchievementService achievementService;

  // TODO push support
//  @GetMapping("/splash/{achievementType}")
//  public ResponseEntity<AchievementSplashPageResponse> getAchievementBySplash(
//      @NotNull @PathVariable("achievementType") final AchievementType achievementType) {
//
//  }

  @GetMapping("/splash/unseen")
  public ResponseEntity<AchievementSplashPageResponse> getUnseenSplash() {
    final User currentUser = onboardingRepository.lookupUserById(
        UserInterceptor.getCurrentUserId());

    final Optional<AchievementSplashTextAndAvatarDto> splashDtoOpt =
        achievementService.calculateCurrentSplash(currentUser);

    if (!splashDtoOpt.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    final AchievementSplashTextAndAvatarDto splashDto = splashDtoOpt.get();

    final StockAvatar stockAvatar = splashDto.getStockAvatar();
    final AvatarUrls stockAvatarUrls = AvatarUrlResolver.getStockAvatarUrls(
        stockAvatar);
    return
        ResponseEntity.ok(
            AchievementSplashPageResponse.builder()
                .achievementType(splashDto.getAchievementType())
                .level(splashDto.getLevel())
                .pageType(AchievementSplashPageType.avatarWithText)
                .headline(splashDto.getHeadline())
                .bodyText(splashDto.getBodyText())
                .avatarUrl(stockAvatarUrls.getAvatarUrl())
                .avatarSvgUrl(stockAvatarUrls.getAvatarSvgUrl())
                .gradientColors(splashDto.getGradientColors())
                .pageIcon("🏆") // https://emojipedia.org/trophy/
                .ackButtonColor(splashDto.getAckButtonColor())
                .ackButtonText(splashDto.getAckButtonText())
                .build());
  }

  @PostMapping("/splash/{achievementType}/ack")
  public void ackSplashSeenForLevel(
      @NotNull @RequestBody AcknowledgeAchievementSplashRequest request,
      @NotNull @PathVariable("achievementType") final AchievementType achievementType) {
    final User currentUser = onboardingRepository.lookupUserById(
        UserInterceptor.getCurrentUserId());

    achievementService.ackSplashSeen(currentUser, achievementType, request.getLevel());

  }

}
