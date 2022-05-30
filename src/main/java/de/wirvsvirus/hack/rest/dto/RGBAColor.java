package de.wirvsvirus.hack.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.wirvsvirus.hack.rest.RGBAJacksonSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

@JsonSerialize(using = RGBAJacksonSerializer.class)
@Value
@Builder
public class RGBAColor {

  int red;
  int green;
  int blue;
  float alpha;

}
