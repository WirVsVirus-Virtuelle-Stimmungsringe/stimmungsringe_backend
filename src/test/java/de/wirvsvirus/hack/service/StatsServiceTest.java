package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class StatsServiceTest {

  private Instant start = Instant.now();
  private UUID userId = UUID.randomUUID();
  private UUID groupId = UUID.randomUUID();

  @Test
  public void sunnyTime() {

    // TODO WIP

    updateStatus(start.plusSeconds(0), Sentiment.sunny, Sentiment.sunnyWithClouds);
    updateStatus(start.plusSeconds(5), Sentiment.cloudy, Sentiment.sunny);
    updateStatus(start.plusSeconds(12), Sentiment.sunny, Sentiment.cloudy);


  }

  private UserGroupMembershipHistory joinGroup(Instant timestamp) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setChange(Change.JOIN);
    return change;
  }


  private UserGroupMembershipHistory leaveGroup(Instant timestamp) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setChange(Change.LEAVE);
    return change;
  }

  private UserStatusChangeHistory updateStatus(Instant timestamp, Sentiment sentiment, Sentiment prevSentiment) {
    final UserStatusChangeHistory change = new UserStatusChangeHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(groupId);
    change.setUserId(userId);
    change.setSentiment(sentiment);
    change.setSentimentText("Some Text!");
    change.setPrevSentiment(prevSentiment);
    return change;
  }

}
