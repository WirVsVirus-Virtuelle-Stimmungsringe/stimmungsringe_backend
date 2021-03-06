package de.wirvsvirus.hack.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationIosOptions {
    NotificationIosDeliveryHeaders headers;
    NotificationIosPayload payload;

    @JsonProperty("fcm_options")
    NotificationIosFcmOptions fcmOptions;
}
