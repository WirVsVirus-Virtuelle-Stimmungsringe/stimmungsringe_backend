package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
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

    final UserStatusChangeHistory ush = new UserStatusChangeHistory();
    ush.setTimestamp(timestamp);
    ush.setGroupId(group.getGroupId());
    ush.setUserId(user.getUserId());
    ush.setSentiment(sentiment);
    ush.setSentimentText(sentimentText);
    ush.setPrevSentiment(prevSentiment);

    final List<UserStatusChangeHistory> historyItems = database.dataRoot()
        .getHistoryUserStatusChanges();
    historyItems.add(ush);
    database.persist(historyItems);
  }
}
