package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserMinimalResponse {

  // not null
  UUID userId;

  // not empty
  String displayName;

  boolean hasName;

  // not empty
  String avatarUrl;

  // nullable
  String avatarSvgUrl;

}

