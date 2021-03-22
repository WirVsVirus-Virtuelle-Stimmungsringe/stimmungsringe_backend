package de.wirvsvirus.hack.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.ToString;

@ToString
public class UserStatus {

  private Instant lastStatusUpdate;
  private Sentiment sentiment;
  private Instant lastSignin;
  /**
   * never null, might be blank, <=20 chars
   */
  private String sentimentText;
  /**
   * map of userIds
   */
  private Set<UUID> kickVotes;

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

  public Instant getLastSignin() {
    return lastSignin;
  }

  public void setLastSignin(Instant lastSignin) {
    this.lastSignin = lastSignin;
  }

  public String getSentimentText() {
    return sentimentText;
  }

  public void setSentimentText(String sentimentText) {
    this.sentimentText = sentimentText;
  }

  public Set<UUID> getKickVotes() {
    return kickVotes;
  }

  public void setKickVotes(Set<UUID> kickVotes) {
    this.kickVotes = kickVotes;
  }
}
