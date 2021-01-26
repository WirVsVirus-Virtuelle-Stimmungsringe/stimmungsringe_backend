package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDataResponse {

  private UUID groupId;
  private String groupName;
  private String groupCode;
}