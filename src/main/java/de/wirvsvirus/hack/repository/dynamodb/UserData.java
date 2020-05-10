package de.wirvsvirus.hack.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import de.wirvsvirus.hack.model.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@DynamoDBTable(tableName = "User")
@NoArgsConstructor
@ToString
public class UserData {

    private UUID userId;
    private String name;
    private String deviceIdentifier;
    private String sentiment;
    private Date lastStatusUpdate;

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
    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(final String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    @DynamoDBAttribute
    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(final String sentiment) {
        this.sentiment = sentiment;
    }

    @DynamoDBAttribute
    public Date getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void setLastStatusUpdate(final Date lastStatusUpdate) {
        this.lastStatusUpdate = lastStatusUpdate;
    }
}
