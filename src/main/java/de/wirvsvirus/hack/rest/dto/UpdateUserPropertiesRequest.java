package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateUserPropertiesRequest {

    @NotEmpty
    public String name;

}
