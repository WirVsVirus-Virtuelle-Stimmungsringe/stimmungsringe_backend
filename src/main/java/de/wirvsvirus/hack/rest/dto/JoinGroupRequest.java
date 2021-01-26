package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class JoinGroupRequest {

    @NotNull
    private UUID groupId;

}
