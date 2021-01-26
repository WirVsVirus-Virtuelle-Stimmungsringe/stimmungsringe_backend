package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnegative;
import java.time.Instant;

@Data
@Builder
public class MyTileResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    @Nonnegative
    private Instant lastStatusUpdate;

}
