package de.wirvsvirus.hack.rest.dto;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SigninUserResponse {

  @NotNull
  String userId;

  boolean hasGroup;

  // null iff hasGroup==false
  @Nullable
  String groupId;

  @Nullable
  String groupName;
}
