package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMinimalResponse {

    // not null
    private UUID userId;

    // not empty
    private String displayName;

    private boolean hasName;

    // not empty
    private String avatarUrl;

}

