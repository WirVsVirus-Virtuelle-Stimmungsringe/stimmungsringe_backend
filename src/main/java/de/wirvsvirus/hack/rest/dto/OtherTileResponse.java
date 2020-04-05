package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.Data;

@Data
public class OtherTileResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

}
