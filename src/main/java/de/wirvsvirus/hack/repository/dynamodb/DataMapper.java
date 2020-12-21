package de.wirvsvirus.hack.repository.dynamodb;

import com.google.common.base.MoreObjects;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.StockAvatar;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.GroupCodeUtil;
import de.wirvsvirus.hack.service.dto.DeviceType;
import java.util.Optional;
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

    public static Message messageFromDatabase(final MessageData messageData) {
        fixupMessage(messageData);
        final Message message = new Message();
        message.setGroupId(messageData.getGroupId());
        message.setMessageId(messageData.getMessageId());
        message.setCreatedAt(messageData.getCreatedAt().toInstant());
        message.setSenderUserId(messageData.getSenderUserId());
        message.setRecipientUserId(messageData.getRecipientUserId());
        message.setText(messageData.getText());
        return message;
    }

    public static MessageData dataFromMessage(final Message message) {
        final MessageData messageData = new MessageData();
        messageData.setMessageId(message.getMessageId());
        messageData.setGroupId(message.getGroupId());
        messageData.setCreatedAt(Date.from(message.getCreatedAt()));
        messageData.setSenderUserId(message.getSenderUserId());
        messageData.setRecipientUserId(message.getRecipientUserId());
        messageData.setText(message.getText());
        return messageData;
    }

    public static UserDeviceData dataFromDevice(final Device device) {
        final UserDeviceData deviceData = new UserDeviceData();
        deviceData.setUserId(device.getUserId());
        deviceData.setDeviceIdentifier(device.getDeviceIdentifier());
        deviceData.setDeviceType(device.getDeviceType().name());
        deviceData.setFcmToken(device.getFcmToken());
        return deviceData;
    }

    public static Device deviceDataFromDatabase(final UserDeviceData deviceData) {
        fixupDevice(deviceData);
        final Device device = new Device();
        device.setUserId(deviceData.getUserId());
        device.setDeviceIdentifier(deviceData.getDeviceIdentifier());
        device.setDeviceType(DeviceType.valueOf(deviceData.getDeviceType()));
        device.setFcmToken(deviceData.getFcmToken());
        return device;
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

    private static void fixupMessage(final MessageData messageData) {
    }

    private static void fixupDevice(final UserDeviceData deviceData) {
        if (deviceData.getDeviceType() == null) {
            deviceData.setDeviceType(DeviceType.ANDROID.name());
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
