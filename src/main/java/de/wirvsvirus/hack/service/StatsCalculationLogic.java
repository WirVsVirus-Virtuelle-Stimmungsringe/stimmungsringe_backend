package de.wirvsvirus.hack.service;

import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MAX;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MIN;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.intersectList;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.truncateListUnit;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import de.wirvsvirus.hack.service.StatsService.MembershipPoints;
import de.wirvsvirus.hack.service.StatsService.StatusPoints;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;

public final class StatsCalculationLogic {


  public static Duration calcSunshineHoursInGroup(
      final List<UserStatusChangeHistory> historyStatusChanges,
      final List<UserGroupMembershipHistory> historyGroupMembership,
      final UUID userId,
      final UUID groupId,
      final Instant now) {

    final List<Pair<Instant, Instant>> membershipIntervals =
        StreamEx.of(historyGroupMembership)
            .filter(h -> h.getUserId().equals(userId))
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

    if (StreamEx.of(historyStatusChanges)
        .noneMatch(h ->
            h.getUserId().equals(userId)
            && h.getGroupId().equals(groupId))) {
      return Duration.ZERO;
    }

    final UserStatusChangeHistory fillLeft = StreamEx.of(historyStatusChanges)
        .filter(h -> h.getUserId().equals(userId))
        .filter(h -> h.getGroupId().equals(groupId))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No status change history!"));

    final UserStatusChangeHistory fillRight = StreamEx.of(historyStatusChanges)
        .filter(h -> h.getUserId().equals(userId))
        .filter(h -> h.getGroupId().equals(groupId))
        .collect(MoreCollectors.last())
        .orElseThrow(() -> new IllegalStateException("No status change history!"));

    // assume that the leftmost status entry documents the status up to that point
    final StatusPoints insertLeft =
        new StatusPoints(INSTANT_MIN, fillLeft.getPrevSentiment(), fillLeft.getPrevSentiment());
    // assume that last status lasts forever
    final StatusPoints insertRight =
        new StatusPoints(INSTANT_MAX, fillRight.getSentiment(), fillRight.getSentiment());

    final List<Pair<Instant, Instant>> sunshineIntervals =
        StreamEx.of(insertLeft).append(
                StreamEx.of(historyStatusChanges)
                    .filter(h -> h.getUserId().equals(userId))
                    .filter(h -> h.getGroupId().equals(groupId))
                    .map(h -> new StatusPoints(h.getTimestamp(), h.getPrevSentiment(),
                        h.getSentiment()))
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

    final List<Pair<Instant, Instant>> combined =
        truncateListUnit(intersectList(
            membershipIntervals, sunshineIntervals), now);

    return StreamEx.of(combined)
        .map(p -> Duration.between(p.getLeft(), p.getRight()))
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);

  }

}
