package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class MyStatusPageResponse {

    private UserMinimalResponse user;

    private Sentiment sentiment;

    private List<SuggestionResponse> suggestions;

}
