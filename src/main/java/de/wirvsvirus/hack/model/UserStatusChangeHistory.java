package de.wirvsvirus.hack.model;

import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;
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

  @NonNull
  @Override
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @NonNull
  public UUID getGroupId() {
    return groupId;
  }

  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  @NonNull
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  @NonNull
  public Sentiment getSentiment() {
    return sentiment;
  }

  public void setSentiment(Sentiment sentiment) {
    this.sentiment = sentiment;
  }

  @NonNull
  public String getSentimentText() {
    return sentimentText;
  }

  public void setSentimentText(String sentimentText) {
    this.sentimentText = sentimentText;
  }

  @NonNull
  public Sentiment getPrevSentiment() {
    return prevSentiment;
  }

  public void setPrevSentiment(Sentiment prevSentiment) {
    this.prevSentiment = prevSentiment;
  }
}
