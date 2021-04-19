package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

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

