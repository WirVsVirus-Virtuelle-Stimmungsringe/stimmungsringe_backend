package de.wirvsvirus.hack.model;

import java.time.Instant;
import lombok.ToString;

@ToString
public class UserStatus {

  private Instant lastStatusUpdate;
  private Sentiment sentiment;

  public Instant getLastStatusUpdate() {
    return lastStatusUpdate;
  }

  public void setLastStatusUpdate(Instant lastStatusUpdate) {
    this.lastStatusUpdate = lastStatusUpdate;
  }

  public Sentiment getSentiment() {
    return sentiment;
  }

  public void setSentiment(Sentiment sentiment) {
    this.sentiment = sentiment;
  }
}
