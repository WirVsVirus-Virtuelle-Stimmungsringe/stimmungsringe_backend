package de.wirvsvirus.hack.rest.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateGroupSettingsRequest {

    @NotEmpty
    public String groupName;

}
