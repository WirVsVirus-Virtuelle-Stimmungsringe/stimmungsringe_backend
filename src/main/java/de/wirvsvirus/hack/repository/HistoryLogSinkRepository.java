package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import java.time.Instant;
import lombok.NonNull;

public interface HistoryLogSinkRepository {

  void logUserUpdatedStatus(
      @NonNull Instant timestamp,
      @NonNull Group group,
      @NonNull User user,
      @NonNull Sentiment sentiment,
      @NonNull String sentimentText,
      @NonNull Sentiment prevSentiment);

  void logUserStartedGroup(
      @NonNull Instant timestamp,
      @NonNull Group group,
      @NonNull User user
  );

  void logUserJoinedGroup(
      @NonNull Instant timestamp,
      @NonNull Group group,
      @NonNull User user
  );

  void logUserLeftGroup(
      @NonNull Instant timestamp,
      @NonNull Group group,
      @NonNull User user
  );
}
