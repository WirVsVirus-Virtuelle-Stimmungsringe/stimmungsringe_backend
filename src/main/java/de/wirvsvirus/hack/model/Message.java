package de.wirvsvirus.hack.model;

import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@ToString
public class Message implements AggregateRoot {

    private UUID groupId;
    private UUID messageId;
    private Instant createdAt;
    private UUID senderUserId;
    private UUID recipientUserId;
    private String text;

}
