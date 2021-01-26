package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class UserMinimalResponse {

    @NotNull
    private UUID userId;

    @NotEmpty
    private String displayName;

    private boolean hasName;

    @NotEmpty
    private String avatarUrl;

}

