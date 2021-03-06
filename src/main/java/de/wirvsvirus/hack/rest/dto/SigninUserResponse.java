package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninUserResponse {

    private String userId;

    private boolean hasGroup;
    private String groupId;
    private String groupName;
}
