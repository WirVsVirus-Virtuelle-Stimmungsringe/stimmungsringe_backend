package de.wirvsvirus.hack.model;

import lombok.ToString;

import java.util.UUID;

@ToString
public class Message {

    private UUID senderUserId;
    private UUID recipientUserId;

    private String text;

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
