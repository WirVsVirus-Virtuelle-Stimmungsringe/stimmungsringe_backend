package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.model.Sentiment;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class RegisterUserRequest {

    @NotEmpty
    private String requestedUsername;

    @NotNull
    private List<Role> roles;

    @NotNull
    private Sentiment sentiment;

}
