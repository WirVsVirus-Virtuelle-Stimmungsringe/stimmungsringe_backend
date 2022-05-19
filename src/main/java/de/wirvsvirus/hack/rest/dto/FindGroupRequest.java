package de.wirvsvirus.hack.rest.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindGroupRequest {

    @NotEmpty
    private String groupCode;
}
