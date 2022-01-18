package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import java.time.Instant;
import javax.annotation.Nonnull;

public interface HistoryRepository {

  void logUserUpdatedStatus(
      @Nonnull Instant timestamp,
      @Nonnull Group group,
      @Nonnull User user,
      @Nonnull Sentiment sentiment,
      @Nonnull String sentimentText,
      @Nonnull Sentiment prevSentiment);

  void logUserStartedGroup(
      @Nonnull Instant timestamp,
      @Nonnull Group group,
      @Nonnull User user
  );

  void logUserJoinedGroup(
      @Nonnull Instant timestamp,
      @Nonnull Group group,
      @Nonnull User user
  );

  void logUserLeftGroup(
      @Nonnull Instant timestamp,
      @Nonnull Group group,
      @Nonnull User user
  );
}
