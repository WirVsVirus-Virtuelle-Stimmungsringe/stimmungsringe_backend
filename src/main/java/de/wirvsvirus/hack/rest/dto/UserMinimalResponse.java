package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserMinimalResponse {

  @NonNull
  UUID userId;

  @Nullable
  String displayName;

  boolean hasName;

  @NonNull
  String avatarUrl;

  @Nullable
  String avatarSvgUrl;

}

