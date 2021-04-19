package de.wirvsvirus.hack.service.dto;

import de.wirvsvirus.hack.model.Group;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignedInDto {

    @NotNull
    private UUID userId;

    private Optional<Group> group;

}
