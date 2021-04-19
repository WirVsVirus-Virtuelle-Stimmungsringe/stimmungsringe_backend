package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageInboxResponse {

    /**
     * descending order
     */
    private List<MessageResponse> messages;

}
