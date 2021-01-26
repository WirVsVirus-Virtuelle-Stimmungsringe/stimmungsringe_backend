package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class LeaveGroupRequest {

    @NotNull
    private UUID groupId;

}
