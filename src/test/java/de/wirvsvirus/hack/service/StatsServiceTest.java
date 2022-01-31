package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.Value;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

public class StatsServiceTest extends AbstractStatsTest {

  @Test
  public void sunnyTime() {

    joinGroup(t0.plusSeconds(0));
    updateStatus(t0.plusSeconds(5), Sentiment.sunny, Sentiment.sunnyWithClouds);
    updateStatus(t0.plusSeconds(10), Sentiment.cloudy, Sentiment.sunny);
    leaveGroup(t0.plusSeconds(30));

    joinGroup(t0.plusSeconds(60));
    updateStatus(t0.plusSeconds(65), Sentiment.sunny, Sentiment.cloudy);
    final Instant now = t0.plusSeconds(80);

    printHistory();

    calcSunshineHoursInGroup(now);
  }



  @Value
  class MembershipPoints {
    Change change;
    Instant timestamp;
  }


  private void calcSunshineHoursInGroup(Instant now) {

    // intersect group membership with sunny status


    // Group membership
    final List<Pair<Instant, Instant>> membershipIntervals =
      StreamEx.of(hist)
        .select(UserGroupMembershipHistory.class)
        .filter(h -> h.getGroupId().equals(groupId))
        .map(hist -> new MembershipPoints(hist.getChange(), hist.getTimestamp()))
        .append(new MembershipPoints(Change.LEAVE, Instant.parse("2055-12-12T12:12:30.00Z")))
        .pairMap((a, b) -> {
          final Optional<Pair<Instant, Instant>> result;
          if (a.getChange() == Change.JOIN && b.getChange() == Change.LEAVE) {
            final Pair<Instant, Instant> pair = Pair.of(a.getTimestamp(), b.getTimestamp());
            result = Optional.of(pair);
          } else {
            result = Optional.empty();
          }
          return result;
        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();

    System.out.println("original " + membershipIntervals);
    System.out.println("truncated " + IntervalIntersectionUtil.truncateListUnit(membershipIntervals, now));


  }

}
