package de.wirvsvirus.hack.model;

import de.wirvsvirus.hack.service.dto.DeviceType;
import lombok.ToString;

import java.util.UUID;

@ToString
public class Device {

    private UUID userId;
    private DeviceType deviceType;
    private String deviceIdentifier;
    private String fcmToken;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
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
