package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class GroupSettingsResponse {

  @NonNull
  UUID groupId;

  @NonNull
  String groupName;

  @NonNull
  String groupCode;
}
