package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageInboxResponse {

    /**
     * descending order
     */
    private List<MessageResponse> messages;

}
