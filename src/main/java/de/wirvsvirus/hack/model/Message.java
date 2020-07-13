package de.wirvsvirus.hack.model;

import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
public class Message {

    private UUID groupId;
    private UUID messageId;
    private Instant createdAt;
    private UUID senderUserId;
    private UUID recipientUserId;
    private String text;

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(final UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(final UUID messageId) {
        this.messageId = messageId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(final UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(final UUID recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
