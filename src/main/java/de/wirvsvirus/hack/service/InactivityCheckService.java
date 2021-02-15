package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InactivityCheckService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InactivityCheckService.class);

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Scheduled(cron = "0 0 19 * * *")
//  @Scheduled(fixedDelay = 5000)
  public void checkForInactiveUsers() {
    final Instant now = Instant.now();
    final Instant cutoff = now.minus(36,
        ChronoUnit.HOURS);

    onboardingRepository.findAllUsers()
        .filter(user ->
            onboardingRepository.findLastSigninByUserId(user.getUserId())
                .isBefore(cutoff))
        .forEach(inactiveUser -> {
          final Optional<Group> group = onboardingRepository
              .findGroupByUser(inactiveUser.getUserId());
          if (!group.isPresent()) {
            return;
          }

          sendPushMessageInactiveUser(inactiveUser, group.get());
          LOGGER.info("Sent retention push message to inactive user {} - last active {}",
              inactiveUser.getUserId(),
              onboardingRepository.findLastStatusUpdateByUserId(inactiveUser.getUserId()));
        });

  }

  private void sendPushMessageInactiveUser(User inactiveUser,
      Group group) {
    onboardingRepository.findDevicesByUserId(inactiveUser.getUserId())
        .forEach(device -> pushNotificationService.sendMessage(
            device.getFcmToken(), "Familiarise  - " + group.getGroupName(),
            inactiveUser.getName() != null
                ? "Schau doch mal wieder rein in deine Familiarise-Gruppe, " + inactiveUser.getName() + "!"
                : "Schau doch mal wieder rein in deine Familiarise-Gruppe!",
            Optional.empty(),
            Optional.empty())
        );
  }

}
