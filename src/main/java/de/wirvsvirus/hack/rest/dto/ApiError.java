package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ApiError {

  @NonNull
  List<String> errors;

}
