package de.wirvsvirus.hack.model;

import java.time.Instant;
import lombok.NonNull;

/**
 * history object with notion of time on a timeline
 */
public interface HistoryObject {

  @NonNull
  Instant getTimestamp();

}
