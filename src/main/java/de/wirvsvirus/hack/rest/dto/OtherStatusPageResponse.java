package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import java.util.List;
import lombok.Data;

@Data
public class OtherStatusPageResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private String sentimentText;

    private List<SuggestionResponse> suggestions;

}
