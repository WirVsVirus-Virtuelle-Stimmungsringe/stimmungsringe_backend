package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.service.dto.DeviceType;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SigninUserRequest {

    /**
     * 1:1 mapping from device to user identity
     */
    @NotEmpty
    private String deviceIdentifier;

    // compat w/ old app
    private String deviceType = DeviceType.ANDROID.name();

    /**
     * firebase push token
     */
    @NotEmpty
    private String fcmToken;

}
