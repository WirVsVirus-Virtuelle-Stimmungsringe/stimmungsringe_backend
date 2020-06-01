package de.wirvsvirus.hack.repository;

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

public interface OnboardingRepository {
//    static Optional<String> findGroupNameForUser(UUID userId) {
//        return Optional.ofNullable(MockFactory.groupByUserId.get(userId));
//    }

    User lookupUserById(UUID userId);

    void updateUser(UUID userId, UserSettingsDto userSettings);

    Group startNewGroup(String groupName, String groupCode);

    void joinGroup(UUID groupId, UUID userId);

    List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId);

    Sentiment findSentimentByUserId(UUID userId);

    Instant findLastStatusUpdateByUserId(UUID userId);

    void updateStatus(UUID userId, Sentiment sentiment);

    Optional<Group> findGroupByUser(UUID userId);

    Optional<Group> findGroupByCode(String groupCode);

    Optional<User> findByDeviceIdentifier(String deviceIdentifier);

    void createNewUser(User newUser, Sentiment sentiment, final Instant lastUpdate);

    Optional<Group> findGroupById(UUID groupId);

    void leaveGroup(UUID groupId, UUID userId);

    void updateGroup(UUID groupId, GroupSettingsDto groupSettings);

    void touchLastStatusUpdate(UUID userId);

    void sendMessage(User sender, User recipient, String text);

    List<Message> findMessagesByUser(UUID userId);
}
