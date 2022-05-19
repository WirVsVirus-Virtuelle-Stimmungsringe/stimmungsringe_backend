package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * list item
 */
@Value
@Builder
public class SunshineHoursResponse {

  @NonNull
  UUID groupId;

  @NonNull
  String groupName;

  // duration format
  @NonNull
  String sunshineHours;
}
