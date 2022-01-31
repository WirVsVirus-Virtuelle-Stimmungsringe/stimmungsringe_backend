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
import org.junit.jupiter.api.Test;

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
    calcSunshineHoursInGroup(now);
  }



  @Value
  class MembershipPoints {
    Instant timestamp;
    Change change;
  }

  @Value
  class StatusPoints {
    Instant timestamp;
    Sentiment prevSentiment;
    Sentiment sentiment;
  }


  private void calcSunshineHoursInGroup(Instant now) {

    // intersect group membership with sunny status


    // Group membership
    
    final List<Pair<Instant, Instant>> membershipIntervals =
      StreamEx.of(hist)
        .select(UserGroupMembershipHistory.class)
        .filter(h -> h.getGroupId().equals(groupId))
        .map(hist -> new MembershipPoints(hist.getTimestamp(), hist.getChange()))
        .append(new MembershipPoints(INSTANT_MAX, Change.LEAVE))
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

    System.out.println("membership " + membershipIntervals);

    final UserStatusChangeHistory fillLeft = StreamEx.of(hist)
        .select(UserStatusChangeHistory.class)
        .filter(h -> h.getGroupId().equals(groupId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("TODO"));// FIXME

    final UserStatusChangeHistory fillRight = StreamEx.of(hist)
        .select(UserStatusChangeHistory.class)
        .filter(h -> h.getGroupId().equals(groupId))
        .collect(MoreCollectors.last())
        .orElseThrow(() -> new IllegalArgumentException("TODO")); // FIXME

    // assume that the leftmost status entry documents the status up to that point
    final StatusPoints insertLeft = new StatusPoints(INSTANT_MIN, fillLeft.getPrevSentiment(),
        fillLeft.getPrevSentiment());
    // assume that last status lasts forever
    final StatusPoints insertRight = new StatusPoints(INSTANT_MAX, fillRight.getSentiment(),
        fillRight.getSentiment());

    final List<Pair<Instant, Instant>> sunshineIntervals =
        StreamEx.of(insertLeft).append(
            StreamEx.of(hist)
                .select(UserStatusChangeHistory.class)
                .filter(h -> h.getGroupId().equals(groupId))
                .map(h -> new StatusPoints(h.getTimestamp(), h.getPrevSentiment(), h.getSentiment()))
        ).append(insertRight)
        .pairMap((a, b) -> {
          Optional<Pair<Instant, Instant>> result;
          if (a.getSentiment() == Sentiment.sunny) {
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

    System.out.println("sunshineIntervals " + sunshineIntervals);


    final List<Pair<Instant, Instant>> combined =
        truncateListUnit(intersectList(
            membershipIntervals, sunshineIntervals), now);

    System.out.println("combined " + combined);

    final Duration total = StreamEx.of(combined)
        .map(p -> Duration.between(p.getLeft(), p.getRight()))
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);

    System.out.println("total=" + total);

  }

}
