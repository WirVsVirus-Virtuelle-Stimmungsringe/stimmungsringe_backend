package de.wirvsvirus.hack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationMessage {

    Notification notification;
    NotificationData data;
    String to;

    @JsonProperty("android")
    NotificationAndroidDeliveryOptions androidDeliveryOptions;

    @JsonProperty("apns")
    NotificationIosOptions iosDeliveryOptions;
}
