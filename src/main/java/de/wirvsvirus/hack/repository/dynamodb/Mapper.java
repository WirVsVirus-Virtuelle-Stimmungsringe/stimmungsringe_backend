package de.wirvsvirus.hack.repository.dynamodb;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class Mapper {

    public static User userFromDatabase(UserData userData) {
        final User user = new User(userData.getUserId(), userData.getDeviceIdentifier());
        user.setName(userData.getName());
        user.setRoles(Collections.emptyList());
        return user;
    }

    public static UserData dataFromUser(User user, Sentiment sentiment) {
        return UserData.builder()
            .userId(user.getUserId())
            .deviceIdentifier(user.getDeviceIdentifier())
            .name(user.getName())
            .sentiment(sentiment.name())
            .build();
    }

    public static Group groupFromDatabase(GroupData groupData) {
        final Group group = new Group(groupData.getGroupId());
        group.setGroupName(group.getGroupName());
        return group;
    }

    public static GroupData dataFromGroup(Group group, List<UUID> members) {
        return GroupData.builder()
            .groupId(group.getGroupId())
            .groupName(group.getGroupName())
            .members(members)
            .build();
    }

    private Mapper() {}
}
