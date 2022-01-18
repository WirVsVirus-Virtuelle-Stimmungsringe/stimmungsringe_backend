package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface OnboardingRepository {
//    static Optional<String> findGroupNameForUser(UUID userId) {
//        return Optional.ofNullable(MockFactory.groupByUserId.get(userId));
//    }

    User lookupUserById(UUID userId);

    boolean isUserExisting(UUID userId);

    void updateUser(UUID userId, UserSettingsDto userSettings);

    Group startNewGroup(String groupName, String groupCode, Instant now);

    void joinGroup(UUID groupId, UUID userId);

    List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId);

    Sentiment findSentimentByUserId(UUID userId);

    String findSentimentTextByUserId(UUID userId);

    Instant findLastStatusUpdateByUserId(UUID userId);

    Instant findLastSigninByUserId(UUID userId);

    void updateStatus(UUID userId, Sentiment sentiment, String sentimentText);

    Optional<Group> findGroupByUser(UUID userId);

    Optional<Group> findGroupByCode(String groupCode);

    Optional<User> findByDeviceIdentifier(String deviceIdentifier);

    void createNewUser(User newUser, Sentiment sentiment, String sentimentText,
        final Instant signinTimestamp);

    Optional<Group> findGroupById(UUID groupId);

    void leaveGroup(UUID groupId, UUID userId);

    void updateGroup(UUID groupId, GroupSettingsDto groupSettings);

    void touchLastStatusUpdate(UUID userId);

    void touchLastSignin(UUID userId);

    void sendMessage(User sender, User recipient, String text);

    List<Message> findMessagesByRecipientId(UUID userId);

    void clearMessagesByRecipientId(UUID userId);

    void addDevice(Device device);

    List<Device> findDevicesByUserId(UUID userId);

    Stream<User> findAllUsers();

    void deleteUser(UUID userId);

}
