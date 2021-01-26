package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateGroupSettingsRequest {

    @NotEmpty
    public String groupName;

}
