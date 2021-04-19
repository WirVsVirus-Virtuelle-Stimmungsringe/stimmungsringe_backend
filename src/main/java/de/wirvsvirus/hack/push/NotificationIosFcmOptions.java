package de.wirvsvirus.hack.push;

import java.net.URI;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationIosFcmOptions {
    URI image;
}
