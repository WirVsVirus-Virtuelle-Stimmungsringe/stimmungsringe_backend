package de.wirvsvirus.hack.service;

import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MAX;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MIN;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.intersectList;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.truncateListUnit;
import com.google.common.collect.Iterables;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.HistoryObject;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import de.wirvsvirus.hack.repository.HistoryQueryRepository;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.spring.Database;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Value;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

  @Autowired
  private HistoryQueryRepository historyQueryRepository;

  @Autowired
  private OnboardingRepository onboardingRepository;

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

  @Scheduled(fixedDelay = 2000)
  public void tickTack() {
    System.out.println("Tick..tack");
    final Instant now = Instant.now();

    onboardingRepository.findAllGroups()
        .filter(group -> "Let the sun shine!".equals(group.getGroupName()))
        .forEach(
        group -> {

          for (User user : onboardingRepository.findAllUsersInGroup(group.getGroupId())) {

            final Duration sunshineDuration =
                StatsCalculationLogic.calcSunshineHoursInGroup(
                    historyQueryRepository.getHistoryOfStatusChanges(),
                    historyQueryRepository.getHistoryUserGroupMembership(),
                    user.getUserId(),
                    group.getGroupId(),
                    now);

              System.out.println("User " + user.getName());
              System.out.println("> " + sunshineDuration);

          } // all users in group


        }
    );

  }

}
