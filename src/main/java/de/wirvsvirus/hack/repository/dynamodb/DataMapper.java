package de.wirvsvirus.hack.repository.dynamodb;

import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.GroupCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Date;
import java.time.Instant;
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
        if (StringUtils.isNotBlank(userData.getStockAvatar())) {
            user.setStockAvatar(StockAvatar.valueOf(userData.getStockAvatar()));
        }
        return user;
    }

    public static UserData dataFromUser(User user, Sentiment sentiment, final Instant lastStatusUpdate) {
        final UserData userData = new UserData();
        userData.setUserId(user.getUserId());
        userData.setDeviceIdentifier(user.getDeviceIdentifier());
        userData.setName(user.getName());
        userData.setSentiment(sentiment.name());
        userData.setLastStatusUpdate(Date.from(lastStatusUpdate));
        if (user.getStockAvatar() != null) {
            userData.setStockAvatar(user.getStockAvatar().name());
        }
        return userData;
    }

    public static Pair<Group, List<UUID>> groupFromDatabase(GroupData groupData) {
        fixupGroup(groupData);
        final Group group = new Group(groupData.getGroupId());
        group.setGroupName(groupData.getGroupName());
        group.setGroupCode(groupData.getGroupCode());
        return Pair.of(group, groupData.getMembers());
    }

    public static GroupData dataFromGroup(Group group, List<UUID> members) {
        final GroupData groupData = new GroupData();
        groupData.setGroupId(group.getGroupId());
        groupData.setGroupName(group.getGroupName());
        groupData.setGroupCode(group.getGroupCode());
        groupData.setMembers(members);
        return groupData;
    }

    private static void fixupUser(final UserData userData) {
    }

    private static void fixupGroup(final GroupData groupData) {
        if (groupData.getGroupName().contains("fixeed")) {
            log.warn("Fixed group name of {}", groupData.getGroupId());
            groupData.setGroupName("Rasselbande");
        }
        if (groupData.getGroupCode() == null) {
            final String code = GroupCodeUtil.generateGroupCode();
            log.warn("Fixing group code of {} with new code <{}>", groupData.getGroupId(), code);
            groupData.setGroupCode(code);
        }
    }

    public static Instant lastStatusUpdateFromDatabase(final UserData userData) {
        if (userData.getLastStatusUpdate() == null) {
            return Instant.now();
        }
        return userData.getLastStatusUpdate().toInstant();
    }

    private DataMapper() {
    }

}
