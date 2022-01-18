package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import de.wirvsvirus.hack.spring.Database;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("microstream")
public class HistoryRepositoryMicrostream {

  @Autowired
  private Database database;
  
  public void logStatusUpdate(
      @Nonnull final Instant timestamp,
      @Nonnull final Group group,
      @Nonnull final User user,
      @Nonnull final Sentiment sentiment,
      @Nonnull final String sentimentText,
      @Nonnull final Sentiment prevSentiment) {

    final UserStatusChangeHistory change = new UserStatusChangeHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(group.getGroupId());
    change.setUserId(user.getUserId());
    change.setSentiment(sentiment);
    change.setSentimentText(sentimentText);
    change.setPrevSentiment(prevSentiment);

    database.dataRoot()
        .getHistoryUserStatusChanges().add(change);
    database.persist(database.dataRoot()
        .getHistoryUserStatusChanges());

    // TODO remove after prod testing of history feature
    log.debug("Write history: {}", change);
  }

  public void logUserGroupJoin(
      @Nonnull final Instant timestamp,
      @Nonnull final Group group,
      @Nonnull final User user
  ) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(group.getGroupId());
    change.setUserId(user.getUserId());
    change.setChange(Change.JOIN);

    database.dataRoot()
        .getHistoryUserGroupMembership().add(change);
    database.persist(database.dataRoot()
        .getHistoryUserGroupMembership());

    log.debug("Write history: {}", change);
  }

  /**
   * note: user gets deleted as soon as he leaves his group
   */
  public void logUserGroupLeave(
      @Nonnull final Instant timestamp,
      @Nonnull final Group group,
      @Nonnull final User user
  ) {
    final UserGroupMembershipHistory change = new UserGroupMembershipHistory();
    change.setTimestamp(timestamp);
    change.setGroupId(group.getGroupId());
    change.setUserId(user.getUserId());
    change.setChange(Change.LEAVE);

    database.dataRoot()
        .getHistoryUserGroupMembership().add(change);
    database.persist(database.dataRoot()
        .getHistoryUserGroupMembership());

    log.debug("Write history: {}", change);
  }

}
