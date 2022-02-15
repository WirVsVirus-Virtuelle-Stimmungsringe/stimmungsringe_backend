package de.wirvsvirus.hack.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.wirvsvirus.hack.model.HistoryObject;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import one.util.streamex.StreamEx;

public abstract class AbstractStatsTest {

  protected final Instant t0 = Instant.parse("2022-02-18T10:15:30.00Z");
  protected final UUID userId = UUID.randomUUID();
  protected final UUID groupId = UUID.randomUUID();
  protected final List<HistoryObject> hist = new LinkedList<>();


  protected List<UserStatusChangeHistory> getHistoryOfStatusChanges() {
    return StreamEx.of(hist)
        .select(UserStatusChangeHistory.class)
        .toList();
  }

  protected List<UserGroupMembershipHistory> getHistoryUserGroupMembership() {
    return StreamEx.of(hist)
        .select(UserGroupMembershipHistory.class)
        .toList();
  }

  protected final void printHistory() {
    hist.forEach(
        entry -> System.out.println("- " + entry)
    );
  }

  protected final void appendToHistory(final HistoryObject change) {
    Preconditions.checkNotNull(change);
    hist.stream()
        .max(Comparator.comparing(HistoryObject::getTimestamp))
        .ifPresent(latest -> {
          Preconditions.checkState(!latest.getTimestamp().isAfter(change.getTimestamp()),
              "Must not insert event older that latest! (%s < %s)",
              latest.toString(), change);
        });
    hist.add(change);
  }

  protected final void joinGroup(Instant timestamp) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setChange(Change.JOIN);

    appendToHistory(change);
  }

  protected void leaveGroup(Instant timestamp) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setChange(Change.LEAVE);

    appendToHistory(change);
  }

  protected void updateStatus(Instant timestamp, Sentiment sentiment, Sentiment prevSentiment) {
    final UserStatusChangeHistory change = new UserStatusChangeHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setSentiment(sentiment);
    change.setSentimentText("Some Text!");
    change.setPrevSentiment(prevSentiment);

    appendToHistory(change);
  }
}
