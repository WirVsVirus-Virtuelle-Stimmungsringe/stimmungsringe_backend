package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class OtherStatusPageResponse {

  @NonNull
  UserMinimalResponse user;

  @NonNull
  Sentiment sentiment;

  @NonNull
  String sentimentText;

  @NonNull
  List<SuggestionResponse> suggestions;

}
