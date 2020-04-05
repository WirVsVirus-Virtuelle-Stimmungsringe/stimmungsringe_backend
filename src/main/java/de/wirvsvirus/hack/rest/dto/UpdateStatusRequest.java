package de.wirvsvirus.hack.rest.dto;

import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.SentimentVO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Sentiment sentimentCode;

}
