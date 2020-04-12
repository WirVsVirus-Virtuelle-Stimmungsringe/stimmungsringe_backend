package de.wirvsvirus.hack.service.exception;

public class GroupNameTakenException extends Throwable {
    private final String groupName;

    public GroupNameTakenException(final String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
