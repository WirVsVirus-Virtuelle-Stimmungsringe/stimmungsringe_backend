package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.exception.PushMessageNotSendException;

import java.util.UUID;

public interface PushNotificationService {

    void registerFcmTokenForUser(UUID userId, String deviceIdentifier, String fcmToken);

    // TODO do we need that?
    String getSenderId();

    void sendMessage(String to, String title, String body) throws PushMessageNotSendException;

}
