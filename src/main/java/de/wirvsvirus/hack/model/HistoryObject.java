package de.wirvsvirus.hack.model;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * history object with notion of time on a timeline
 */
public interface HistoryObject {

  @Nonnull
  Instant getTimestamp();

}
