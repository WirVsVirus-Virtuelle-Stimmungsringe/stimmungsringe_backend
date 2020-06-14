package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageResponse {

    private Instant createdAt;
    private UserMinimalResponse senderUser;
    private String text;

}
