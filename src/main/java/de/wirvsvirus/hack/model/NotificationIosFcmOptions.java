package de.wirvsvirus.hack.model;

import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class NotificationIosFcmOptions {
    URI image;
}
