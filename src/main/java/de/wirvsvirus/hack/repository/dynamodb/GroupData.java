package de.wirvsvirus.hack.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Builder;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@DynamoDBTable(tableName = "Group")
@ToString
@Builder
public class GroupData {

    private UUID userId;
    private String groupName;
    private List<UUID> members;

    @DynamoDBHashKey
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @DynamoDBHashKey
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    @DynamoDBHashKey
    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(final List<UUID> members) {
        this.members = members;
    }
}
