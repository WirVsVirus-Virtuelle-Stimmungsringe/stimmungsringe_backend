package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyTileResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private String sentimentText;

    private Instant lastStatusUpdate;

}
