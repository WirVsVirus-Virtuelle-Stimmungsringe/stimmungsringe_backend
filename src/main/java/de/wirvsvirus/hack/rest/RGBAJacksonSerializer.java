package de.wirvsvirus.hack.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.wirvsvirus.hack.rest.dto.RGBAColor;
import java.io.IOException;

public class RGBAJacksonSerializer extends StdSerializer<RGBAColor> {

  public RGBAJacksonSerializer() {
    this(null);
  }

  public RGBAJacksonSerializer(Class<RGBAColor> t) {
    super(t);
  }

  @Override
  public void serialize(RGBAColor rgbaColor, JsonGenerator jgen,
      SerializerProvider provider) throws IOException {

    jgen.writeStartObject();
    jgen.writeFieldName("rgba");
    jgen.writeStartArray();
    jgen.writeNumber(rgbaColor.getRed());
    jgen.writeNumber(rgbaColor.getGreen());
    jgen.writeNumber(rgbaColor.getBlue());
    jgen.writeNumber(rgbaColor.getAlpha());
    jgen.writeEndArray();
    jgen.writeEndObject();

  }
}
