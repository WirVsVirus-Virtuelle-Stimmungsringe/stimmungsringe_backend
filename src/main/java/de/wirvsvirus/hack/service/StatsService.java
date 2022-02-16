package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.repository.HistoryQueryRepository;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatsService {

  @Autowired
  private HistoryQueryRepository historyQueryRepository;

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Value
  static
  class MembershipPoints {
    Instant timestamp;
    Change change;
  }

  @Value
  static
  class StatusPoints {
    Instant timestamp;
    Sentiment prevSentiment;
    Sentiment sentiment;
  }

  @Scheduled(cron = "0 0 19 * * SUN", zone = "Europe/Berlin")
//  @Scheduled(fixedDelay = 5000)
  public void calcSunshineHoursAndSendPushes() {
    final AtomicInteger sent = new AtomicInteger();
    final AtomicLong totalHours = new AtomicLong();
    final Instant now = Instant.now();

    onboardingRepository.findAllGroups()
        .forEach(
            group -> {
              final Duration sunshine = calcSunshineTimeForGroup(group.getGroupId(), now);

              if (sunshine.toHours() < 2) {
                return;
              }

              for (final User user : onboardingRepository.findAllUsersInGroup(group.getGroupId())) {
                final long hours = sunshine.toHours();
                log.info("> {} collected {} hours of sunshine", user, hours);
                sendPushSunshineHours(user, group, hours);
                sent.incrementAndGet();
                totalHours.addAndGet(hours);
              }
            }
        );

    log.info("Sunshine-Hours-Stats job sent {} messages with total of {} sunny hours",
        sent.get(), totalHours.get());
  }


  public Duration calcSunshineTimeForGroup(final UUID groupId, final Instant now) {

    Duration sum = Duration.ZERO;

    for (final User user : onboardingRepository.findAllUsersInGroup(groupId)) {

      final Duration sunshineDuration =
          StatsCalculationLogic.calcSunshineHoursInGroup(
              historyQueryRepository.getHistoryOfStatusChanges(),
              historyQueryRepository.getHistoryUserGroupMemberships(),
              user.getUserId(),
              groupId,
              now);

      sum = sum.plus(sunshineDuration);

    } // all users in group

    return sum;
  }

  private void sendPushSunshineHours(
      final User user, final Group group, final long hours) {
    onboardingRepository.findDevicesByUserId(user.getUserId())
        .forEach(device -> pushNotificationService.sendMessage(
            device.getFcmToken(), "Familiarise  - " + group.getGroupName(),
            String.format("☀️ Ihr hattet %d Sonnenstunden in dieser Woche!", hours),
            Optional.empty(),
            Optional.empty())
        );
  }

}
