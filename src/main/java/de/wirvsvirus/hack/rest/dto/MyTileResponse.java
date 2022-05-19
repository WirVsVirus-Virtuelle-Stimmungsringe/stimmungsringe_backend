package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MyTileResponse {

  @NonNull
  UserMinimalResponse user;

  @NonNull
  Sentiment sentiment;

  @NonNull
  String sentimentText;

  @NonNull
  Instant lastStatusUpdate;

}
