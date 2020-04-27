package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSettingsResponse {

    private String userName;

    private boolean hasName;
}
