package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupSettingsResponse {

    private UUID groupId;
    private String groupName;
    private String groupCode;
    // FIXME
    private String userName;
}
