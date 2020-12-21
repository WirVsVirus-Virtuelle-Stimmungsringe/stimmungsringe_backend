package de.wirvsvirus.hack.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JacksonUtil {

  private final static ObjectMapper MAPPER = new ObjectMapper();

  public static String prettyPrint(final Object raw) {
    if (raw == null) {
      return null;
    }
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(raw);
    } catch (final JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  private JacksonUtil() {}
}
