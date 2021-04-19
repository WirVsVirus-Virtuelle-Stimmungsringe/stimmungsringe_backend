package de.wirvsvirus.hack.rest.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {

    private Instant createdAt;
    private UserMinimalResponse senderUser;
    private String text;

}
