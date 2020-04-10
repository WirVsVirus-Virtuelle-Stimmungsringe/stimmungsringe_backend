package de.wirvsvirus.hack.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
public class UserSignedInDto {

    @NotNull
    private UUID userId;

    private Optional<String> groupName;

}
