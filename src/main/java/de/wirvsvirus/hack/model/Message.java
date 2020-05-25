package de.wirvsvirus.hack.model;

import lombok.ToString;

import java.util.UUID;

@ToString
public class Message {

    private UUID senderUserId;
    private UUID receipientUserId;

    private String text;

    public UUID getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(final UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    public UUID getReceipientUserId() {
        return receipientUserId;
    }

    public void setReceipientUserId(final UUID receipientUserId) {
        this.receipientUserId = receipientUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
