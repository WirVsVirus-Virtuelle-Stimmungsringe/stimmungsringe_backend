package de.wirvsvirus.hack.model;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.ToString;

@ToString
public class UserGroupMembershipHistory implements HistoryObject, MicrostreamObject {

  public enum Change {
    START,
    JOIN,
    /**
     * note: user gets deleted upon group leave
     */
    LEAVE;
  }

  private Instant timestamp;
  private UUID groupId;
  private UUID userId;
  private Change change;

  @Override
  @Nonnull
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
  public Change getChange() {
    return change;
  }

  public void setChange(Change change) {
    this.change = change;
  }
}
