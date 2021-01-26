package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StartNewGroupRequest {

    @NotEmpty
    private String groupName;

}
