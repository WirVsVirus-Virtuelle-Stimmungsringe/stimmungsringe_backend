package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AvailableMessagesResponse {

  @NonNull
  List<MessageTemplate> messageTemplates;

}
