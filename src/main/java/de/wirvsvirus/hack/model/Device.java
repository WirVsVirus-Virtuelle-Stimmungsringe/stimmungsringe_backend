package de.wirvsvirus.hack.model;

import lombok.ToString;

import java.util.UUID;

@ToString
public class Device {

    private UUID userId;
    private String deviceIdentifier;
    private String fcmToken;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(final String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(final String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
