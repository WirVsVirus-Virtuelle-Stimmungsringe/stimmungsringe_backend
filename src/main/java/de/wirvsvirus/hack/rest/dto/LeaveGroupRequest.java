package de.wirvsvirus.hack.rest.dto;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveGroupRequest {

    @NotNull
    private UUID groupId;

}
