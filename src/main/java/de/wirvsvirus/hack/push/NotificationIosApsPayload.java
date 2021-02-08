package de.wirvsvirus.hack.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationIosApsPayload {
    @JsonProperty("mutable-content")
    MutableContent mutableContent;

    public enum MutableContent {
        FALSE(0),
        TRUE(1);

        int value;

        MutableContent(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }
    }

}
