package de.wirvsvirus.hack.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationIosPayload {
    @JsonProperty("aps")
    NotificationIosApsPayload notificationIosApsPayload;
}
