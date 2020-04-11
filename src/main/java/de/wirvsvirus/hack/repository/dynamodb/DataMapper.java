package de.wirvsvirus.hack.repository.dynamodb;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
public final class DataMapper {

    public static User userFromDatabase(UserData userData) {
        fixupUser(userData);
        final User user = new User(userData.getUserId(), userData.getDeviceIdentifier());
        user.setName(userData.getName());
        user.setRoles(Collections.emptyList());
        return user;
    }

    public static UserData dataFromUser(User user, Sentiment sentiment) {
        final UserData userData = new UserData();
        userData.setUserId(user.getUserId());
        userData.setDeviceIdentifier(user.getDeviceIdentifier());
        userData.setName(user.getName());
        userData.setSentiment(sentiment.name());
        return userData;
    }

    public static Pair<Group, List<UUID>> groupFromDatabase(GroupData groupData) {
        fixupGroup(groupData);
        final Group group = new Group(groupData.getGroupId());
        group.setGroupName(groupData.getGroupName());
        return Pair.of(group, groupData.getMembers());
    }

    public static GroupData dataFromGroup(Group group, List<UUID> members) {
        final GroupData groupData = new GroupData();
        groupData.setGroupId(group.getGroupId());
        groupData.setGroupName(group.getGroupName());
        groupData.setMembers(members);
        return groupData;
    }

    private static void fixupUser(final UserData userData) {
    }

    private static void fixupGroup(final GroupData groupData) {
        if (groupData.getGroupName() == null) {
            log.warn("Fixed group name of {}", groupData.getGroupId());
            groupData.setGroupName("fixed empty name");
        }
    }

    private DataMapper() {}
}
