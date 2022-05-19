package de.wirvsvirus.hack.rest.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MessageResponse {

  @NonNull
  Instant createdAt;

  @NonNull
  UserMinimalResponse senderUser;

  @NonNull
  String text;
}
