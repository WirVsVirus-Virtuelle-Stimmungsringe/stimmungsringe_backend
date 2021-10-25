package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InactivityCheckService {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Scheduled(cron = "0 0 19 * * *")
//  @Scheduled(fixedDelay = 5000)
  public void checkForInactiveUsers() {

    final Set<UUID> deduplicationSet = new HashSet<>();

    pokeInactiveUsers(deduplicationSet);

    pokeUsersWithoutStatusUpdate(deduplicationSet);

  }

  private void pokeInactiveUsers(final Set<UUID> deduplicationSet) {
    final Instant cutoffNoSignin = Instant.now().minus(
        48, ChronoUnit.HOURS);

    onboardingRepository.findAllUsers()
        .filter(user -> !deduplicationSet.contains(user.getUserId()))
        .filter(user ->
            onboardingRepository.findLastSigninByUserId(user.getUserId())
                .isBefore(cutoffNoSignin))
        .forEach(inactiveUser -> {
          final Optional<Group> group = onboardingRepository
              .findGroupByUser(inactiveUser.getUserId());
          if (!group.isPresent()) {
            return;
          }

          sendPushMessageInactiveUser(inactiveUser, group.get());
          deduplicationSet.add(inactiveUser.getUserId());
          log.info("Sent retention push message to inactive user {} - last active {}",
              inactiveUser.getUserId(),
              onboardingRepository.findLastSigninByUserId(inactiveUser.getUserId()));
        });
  }

  private void pokeUsersWithoutStatusUpdate(final Set<UUID> deduplicationSet) {
    final Instant cutoffNoStatusUpdate = Instant.now().minus(
        36, ChronoUnit.HOURS);

    onboardingRepository.findAllUsers()
        .filter(user -> !deduplicationSet.contains(user.getUserId()))
        .filter(user -> onboardingRepository.findLastStatusUpdateByUserId(user.getUserId())
            .isBefore(cutoffNoStatusUpdate))
        .forEach(lazyUser -> {
          final Optional<Group> group = onboardingRepository
              .findGroupByUser(lazyUser.getUserId());
          if (!group.isPresent()) {
            return;
          }

          sendPushMessageLazyUser(lazyUser, group.get());
          deduplicationSet.add(lazyUser.getUserId());
          log.info("Sent retention push message to user {} with no status update since {}",
              lazyUser.getUserId(),
              onboardingRepository.findLastStatusUpdateByUserId(lazyUser.getUserId()));
        });
  }

  private void sendPushMessageInactiveUser(
      final User inactiveUser, final Group group) {
    onboardingRepository.findDevicesByUserId(inactiveUser.getUserId())
        .forEach(device -> pushNotificationService.sendMessage(
            device.getFcmToken(), "Familiarise  - " + group.getGroupName(),
            inactiveUser.getName() != null
                ? "Schau doch mal wieder rein in deine Fam-Group, " + inactiveUser.getName() + "!"
                : "Schau doch mal wieder rein in deine Fam-Group!",
            Optional.empty(),
            Optional.empty())
        );
  }

  private void sendPushMessageLazyUser(
      final User lazyUser, final Group group) {
    onboardingRepository.findDevicesByUserId(lazyUser.getUserId())
        .forEach(device -> pushNotificationService.sendMessage(
            device.getFcmToken(), "Familiarise  - " + group.getGroupName(),
            lazyUser.getName() != null
                ? "Wie geht es dir gerade, " + lazyUser.getName() + "!"
                : "Wie geht es dir gerade!",
            Optional.empty(),
            Optional.empty())
        );
  }

}
