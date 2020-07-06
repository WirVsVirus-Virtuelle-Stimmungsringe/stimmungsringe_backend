package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SigninUserRequest {

    /**
     * 1:1 mapping from device to user identity
     */
    @NotNull
    private String deviceIdentifier;

    /**
     * firebase push token
     */
    private String fcmToken;

}
