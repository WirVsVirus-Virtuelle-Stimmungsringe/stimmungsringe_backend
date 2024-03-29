package de.wirvsvirus.hack.service;

import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MAX;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.INSTANT_MIN;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.intersectList;
import static de.wirvsvirus.hack.service.IntervalIntersectionUtil.truncateListUnit;
import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import de.wirvsvirus.hack.service.StatsService.MembershipPoints;
import de.wirvsvirus.hack.service.StatsService.StatusPoints;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
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

    final List<UserStatusChangeHistory> filteredChanges =
        StreamEx.of(
                historyStatusChanges)
            .filter(h -> h.getUserId().equals(userId))
            .filter(h -> h.getGroupId().equals(groupId))
            .toList();

    final List<UserGroupMembershipHistory> filteredMembership =
        StreamEx.of(
                historyGroupMembership)
            .filter(h -> h.getUserId().equals(userId))
            .filter(h -> h.getGroupId().equals(groupId))
            .toList();

    return calcSunshineHoursInGroup(
        filteredChanges,
        filteredMembership,
        now);
  }

  public static Duration calcSunshineHoursInGroup(
      final List<UserStatusChangeHistory> historyStatusChanges,
      final List<UserGroupMembershipHistory> historyGroupMembership,
      final Instant now) {

    Preconditions.checkState(
        historyStatusChanges.stream()
            .map(UserStatusChangeHistory::getUserId)
            .distinct()
            .count() <= 1,
        "Must pass in history of exactly one user"
    );

    Preconditions.checkState(
        historyStatusChanges.stream()
            .map(UserStatusChangeHistory::getGroupId)
            .distinct()
            .count() <= 1,
        "Must pass in history of exactly one group"
    );

    Preconditions.checkState(
        historyGroupMembership.stream()
            .map(UserGroupMembershipHistory::getUserId)
            .distinct()
            .count() <= 1,
        "Must pass in history of exactly one user"
    );

    Preconditions.checkState(
        historyGroupMembership.stream()
            .map(UserGroupMembershipHistory::getGroupId)
            .distinct()
            .count() <= 1,
        "Must pass in history of exactly one group"
    );

    if (historyStatusChanges.isEmpty()) {
      return Duration.ZERO;
    }

    final Instant startOfWeek =
        now.atZone(ZoneId.of("Europe/Berlin"))
            .truncatedTo(ChronoUnit.DAYS)
            .with(ChronoField.DAY_OF_WEEK, 1)
            .toInstant();

    final List<Pair<Instant, Instant>> membershipIntervals =
        StreamEx.of(historyGroupMembership)
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

    final UserStatusChangeHistory fillLeft = StreamEx.of(historyStatusChanges)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No status change history!"));

    final UserStatusChangeHistory fillRight = StreamEx.of(historyStatusChanges)
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
            membershipIntervals, sunshineIntervals), startOfWeek, now);

    return StreamEx.of(combined)
        .map(p -> Duration.between(p.getLeft(), p.getRight()))
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);

  }

}
