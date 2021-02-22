package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Sentiment sentiment;

    private String sentimentText;

}
