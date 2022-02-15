package de.wirvsvirus.hack.service;

import com.google.common.collect.Iterables;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserRetentionJob {

  /**
   * limit number of names in enumeration
   */
  private static final int MAX_USER_NAMES = 3;

  /**
   * use only short names in push message
   */
  private static final int USER_NAME_LENGTH_LIMIT = 12;

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Scheduled(cron = "0 0 19 * * *", zone = "Europe/Berlin")
//  @Scheduled(fixedDelay = 5000)
  public void checkForInactiveUsers() {

    final Set<UUID> deduplicationSet = new HashSet<>();

    pokeInactiveUsers(deduplicationSet);

    pokeUsersWithoutStatusUpdate(deduplicationSet);

  }

  private void pokeInactiveUsers(final Set<UUID> deduplicationSet) {
    final AtomicInteger sent = new AtomicInteger();
    final AtomicInteger error = new AtomicInteger();
    final long cutoffNoSignInDays = 2;

    onboardingRepository
        .findAllUsers()
        .filter(user -> !deduplicationSet.contains(user.getUserId()))
        .filter(
            user -> {
              final long daysSinceLastSignIn =
                  fullDaysElapsedSince(
                      onboardingRepository.findLastSigninByUserId(user.getUserId()));
              return daysSinceLastSignIn > cutoffNoSignInDays
                  && AlgoUtil.isFibonacciNumber(daysSinceLastSignIn);
            })
        .forEach(
            inactiveUser -> {
              try {
                final Optional<Group> group =
                    onboardingRepository.findGroupByUser(inactiveUser.getUserId());
                if (!group.isPresent()) {
                  return;
                }

                sendPushMessageInactiveUser(inactiveUser, group.get());
                deduplicationSet.add(inactiveUser.getUserId());
                log.info(
                    "Sent retention push message to inactive user {} - last active {}",
                    inactiveUser.getUserId(),
                    onboardingRepository.findLastSigninByUserId(inactiveUser.getUserId()));
                sent.incrementAndGet();
              } catch (final Exception ex) {
                log.warn("Skip processing inactive user {} due to error", inactiveUser, ex);
                error.incrementAndGet();
              }
            });

    log.info("Retention job for inactive users sent {} messages and skipped {} users",
        sent.get(), error.get());
  }

  private void pokeUsersWithoutStatusUpdate(final Set<UUID> deduplicationSet) {
    final AtomicInteger sent = new AtomicInteger();
    final AtomicInteger error = new AtomicInteger();
    final long cutoffNoStatusUpdateDays = 1;

    onboardingRepository
        .findAllUsers()
        .filter(user -> !deduplicationSet.contains(user.getUserId()))
        .filter(
            user -> {
              final long daysSinceLastStatusUpdate =
                  fullDaysElapsedSince(
                      onboardingRepository.findLastStatusUpdateByUserId(user.getUserId()));
              return daysSinceLastStatusUpdate > cutoffNoStatusUpdateDays
                  && AlgoUtil.isFibonacciNumber(daysSinceLastStatusUpdate);
            })
        .forEach(
            lazyUser -> {
              try {
                final Optional<Group> group =
                    onboardingRepository.findGroupByUser(lazyUser.getUserId());
                if (!group.isPresent()) {
                  return;
                }

                sendPushMessageLazyUser(lazyUser, group.get());
                deduplicationSet.add(lazyUser.getUserId());
                log.info(
                    "Sent retention push message to user {} with no status update since {}",
                    lazyUser.getUserId(),
                    onboardingRepository.findLastStatusUpdateByUserId(lazyUser.getUserId()));
                sent.incrementAndGet();
              } catch (final Exception ex) {
                log.warn("Skip processing lazy user {} due to error", lazyUser, ex);
                error.incrementAndGet();
              }
            });

    log.info("Retention job for lazy users sent {} messages and skipped {} users",
        sent.get(), error.get());
  }

  private long fullDaysElapsedSince(final Instant timestamp) {
    return Duration.between(timestamp, Instant.now()).toDays();
  }

  private void sendPushMessageInactiveUser(final User inactiveUser, final Group group) {
    onboardingRepository
        .findDevicesByUserId(inactiveUser.getUserId())
        .forEach(
            device ->
                pushNotificationService.sendMessage(
                    device.getFcmToken(),
                    "Familiarise - " + group.getGroupName(),
                    "Schau doch mal wieder rein in deine Fam-Group"
                        + (inactiveUser.getName() != null
                            ? ", " + inactiveUser.getName() + "!"
                            : "!"),
                    Optional.empty(),
                    Optional.empty()));
  }

  private void sendPushMessageLazyUser(
      final User lazyUser, final Group group) {

    final List<String> otherUserNames =
        onboardingRepository
            .findOtherUsersInGroup(group.getGroupId(), lazyUser.getUserId()).stream()
            .filter(User::hasName)
            .map(User::getName)
            .collect(Collectors.toList());
    Collections.shuffle(otherUserNames);

    onboardingRepository.findDevicesByUserId(lazyUser.getUserId())
        .forEach(device -> pushNotificationService.sendMessage(
            device.getFcmToken(), "Familiarise - " + group.getGroupName(),
            buildNoStatusUpdateString(otherUserNames),
            Optional.empty(),
            Optional.empty())
        );
  }

  static String buildNoStatusUpdateString(final List<String> allOtherUserNames) {
    final List<String> otherUserNames =
        allOtherUserNames.stream()
            .filter(name -> name.length() <= USER_NAME_LENGTH_LIMIT)
            .limit(MAX_USER_NAMES)
            .collect(Collectors.toList());

    final String pushText;
    if (otherUserNames.isEmpty()) {
      pushText = "Wie geht es dir gerade?";
    } else if (otherUserNames.size() == 1) {
      pushText = String.format("%s möchte wissen, wie es dir geht!",
          Iterables.getOnlyElement(otherUserNames));
    } else {
      final String allButLast =
          StreamEx.of(otherUserNames)
              .limit(otherUserNames.size() - 1L)
              .joining(", ");
      final String last = Iterables.getLast(otherUserNames);
      pushText = String.format("%s und %s möchten wissen, wie es dir geht!",
          allButLast, last);
    }
    return pushText;
  }

}
