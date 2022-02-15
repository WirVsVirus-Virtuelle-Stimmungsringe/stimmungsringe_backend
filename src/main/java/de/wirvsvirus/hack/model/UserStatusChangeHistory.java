package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import lombok.ToString;

/**
 * changed status/text, might be a noop
 */
@ToString
public class UserStatusChangeHistory implements HistoryObject, MicrostreamObject {

  private Instant timestamp;
  private UUID groupId;
  private UUID userId;
  private Sentiment sentiment;
  private String sentimentText;
  private Sentiment prevSentiment;

  @Nonnull
  @Override
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @Nonnull
  public UUID getGroupId() {
    return groupId;
  }

  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  @Nonnull
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  @Nonnull
  public Sentiment getSentiment() {
    return sentiment;
  }

  public void setSentiment(Sentiment sentiment) {
    this.sentiment = sentiment;
  }

  @Nonnull
  public String getSentimentText() {
    return sentimentText;
  }

  public void setSentimentText(String sentimentText) {
    this.sentimentText = sentimentText;
  }

  @Nonnull
  public Sentiment getPrevSentiment() {
    return prevSentiment;
  }

  public void setPrevSentiment(Sentiment prevSentiment) {
    this.prevSentiment = prevSentiment;
  }
}
