package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.service.dto.DeviceType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SigninUserRequest {

    /**
     * 1:1 mapping from device to user identity
     */
    @NotNull
    private String deviceIdentifier;

    // compat w/ old app
    private String deviceType = DeviceType.ANDROID.name();

    /**
     * firebase push token
     */
    private String fcmToken;

}
