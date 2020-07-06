package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.exception.PushMessageNotSendException;

import java.util.UUID;

public interface PushNotificationService {

    void registerFcmTokenForUser(UUID userId, String deviceIdentifier, String fcmToken);

    String getSenderId();

    void sendMessage(String receiptId, String title, String body) throws PushMessageNotSendException;

}
