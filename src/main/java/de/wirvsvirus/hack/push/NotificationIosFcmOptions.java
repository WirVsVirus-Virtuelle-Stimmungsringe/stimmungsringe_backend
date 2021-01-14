package de.wirvsvirus.hack.push;

import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class NotificationIosFcmOptions {
    URI image;
}
