package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OtherTileResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private Instant lastStatusUpdate;

}
