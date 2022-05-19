package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MessageTemplate {

  boolean used;

  @NonNull
  String text;

}
