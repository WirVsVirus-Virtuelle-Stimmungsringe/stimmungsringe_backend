package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import java.util.List;
import lombok.Data;

@Data
public class MyStatusPageResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private List<SuggestionResponse> suggestions;

    private String sentimentText;

}
