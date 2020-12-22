package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.service.dto.DeviceType;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public interface PushNotificationService {

    void registerFcmTokenForUser(UUID userId, String deviceIdentifier, DeviceType android,
        String fcmToken);

    // TODO do we need that?
    String getSenderId();

    void sendMessage(String to, String title, String body, Optional<String> collapseId, Optional<URI> imageUri);

}
