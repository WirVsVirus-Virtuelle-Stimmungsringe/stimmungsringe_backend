package de.wirvsvirus.hack.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import de.wirvsvirus.hack.model.Sentiment;
import lombok.Builder;
import lombok.ToString;

import java.util.UUID;

@DynamoDBTable(tableName = "User")
@ToString
@Builder
public class UserData {

    private UUID userId;
    private String name;
    private Sentiment sentiment;

    @DynamoDBHashKey
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @DynamoDBHashKey
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @DynamoDBHashKey
    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(final Sentiment sentiment) {
        this.sentiment = sentiment;
    }

}
