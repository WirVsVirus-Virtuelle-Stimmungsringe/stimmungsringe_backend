package de.wirvsvirus.hack.service;

import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.*;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class StatsServiceTest extends AbstractStatsTest {

  @Test
  public void sunnyTime() {

    joinGroup(t0.plusSeconds(0));
    // 5 sec
    updateStatus(t0.plusSeconds(5), Sentiment.sunnyWithClouds, Sentiment.sunny);
    updateStatus(t0.plusSeconds(25), Sentiment.sunny, Sentiment.sunnyWithClouds);
    // 7 sec
    leaveGroup(t0.plusSeconds(32));

    joinGroup(t0.plusSeconds(60));
    // 9 sec
    updateStatus(t0.plusSeconds(69), Sentiment.cloudy, Sentiment.sunny);
    updateStatus(t0.plusSeconds(75), Sentiment.sunny, Sentiment.cloudy);
    // 6 sec
    final Instant now = t0.plusSeconds(81);

    printHistory();

     // 27 sec
    final Duration sunshine = StatsCalculationLogic.calcSunshineHoursInGroup(
        getHistoryOfStatusChanges(), getHistoryUserGroupMembership(), userId, groupId, now);
    Assertions.assertEquals(Duration.ofSeconds(27), sunshine);
  }

}
