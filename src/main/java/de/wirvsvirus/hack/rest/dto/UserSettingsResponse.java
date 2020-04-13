package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSettingsResponse {

    private String userName;
}
