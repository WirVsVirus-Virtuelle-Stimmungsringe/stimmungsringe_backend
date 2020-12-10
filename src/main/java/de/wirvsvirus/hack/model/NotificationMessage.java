package de.wirvsvirus.hack.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationMessage {

    Notification notification;
    String priority;
    NotificationData data;
    String to;
}
