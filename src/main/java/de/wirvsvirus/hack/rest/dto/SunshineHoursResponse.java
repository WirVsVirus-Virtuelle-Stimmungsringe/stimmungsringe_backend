package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * list item
 */
@Data
@Builder
public class SunshineHoursResponse {
  UUID groupId;
  String groupName;
  // duration format
  String sunshineHours;
}
