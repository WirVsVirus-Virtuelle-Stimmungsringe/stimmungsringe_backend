package de.wirvsvirus.hack.model;

import com.google.common.base.Preconditions;
import lombok.ToString;

import java.util.UUID;

@ToString
public class Group implements AggregateRoot {

    private final UUID groupId;
    private String groupName;
    private String groupCode;

    public Group(final UUID groupId) {
        Preconditions.checkNotNull(groupId);
        this.groupId = groupId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(final String groupCode) {
        this.groupCode = groupCode;
    }
}
