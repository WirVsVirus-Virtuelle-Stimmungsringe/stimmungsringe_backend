package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SuggestionResponse {

  @NonNull
  String text;

}
