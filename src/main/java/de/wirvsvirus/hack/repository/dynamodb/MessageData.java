package de.wirvsvirus.hack.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@DynamoDBTable(tableName = "Message")
@NoArgsConstructor
@ToString
public class MessageData {

    private UUID groupId;
    private UUID messageId;

    private Instant createdAt;
    private UUID senderUserId;
    private UUID recipientUserId;
    private String text;

    @DynamoDBHashKey
    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(final UUID groupId) {
        this.groupId = groupId;
    }

    @DynamoDBHashKey
    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(final UUID messageId) {
        this.messageId = messageId;
    }

    @DynamoDBAttribute
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDBAttribute
    public UUID getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(final UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    @DynamoDBAttribute
    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(final UUID recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    @DynamoDBAttribute
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
