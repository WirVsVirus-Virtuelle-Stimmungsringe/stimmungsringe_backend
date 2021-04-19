package de.wirvsvirus.hack.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Message implements MicrostreamObject {

    private UUID groupId;
    private UUID messageId;
    private Instant createdAt;
    private UUID senderUserId;
    private UUID recipientUserId;
    private String text;

}
