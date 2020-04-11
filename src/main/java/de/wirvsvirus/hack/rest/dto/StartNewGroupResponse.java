package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StartNewGroupResponse {

    private UUID groupId;
    private String groupName;
}
