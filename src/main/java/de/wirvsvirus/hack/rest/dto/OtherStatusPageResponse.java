package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class OtherStatusPageResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private List<SuggestionResponse> suggestions;

}
