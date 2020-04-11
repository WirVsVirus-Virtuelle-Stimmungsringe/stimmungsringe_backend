package de.wirvsvirus.hack.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
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
    private String sentiment;

    @DynamoDBHashKey
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(final String sentiment) {
        this.sentiment = sentiment;
    }

}
