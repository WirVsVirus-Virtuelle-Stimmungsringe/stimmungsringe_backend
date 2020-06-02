package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("!dynamodb")
public class OnboardingRepositoryInMemory implements OnboardingRepository {

    @PostConstruct
    public void initMock() {

        final Group rasselbande = startNewGroup("Rasselbande", "12345");

        joinGroup(rasselbande.getGroupId(), UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef"));
        touchLastStatusUpdate(UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef"));
        joinGroup(rasselbande.getGroupId(), UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb"));
        touchLastStatusUpdate(UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb"));
        joinGroup(rasselbande.getGroupId(), UUID.fromString("deadbeef-b855-46ba-b907-321d01010101"));
        touchLastStatusUpdate(UUID.fromString("deadbeef-b855-46ba-b907-321d01010101"));

        log.info("Created mock group " + rasselbande);
    }

    @Override
    public void createNewUser(final User newUser, Sentiment sentiment, final Instant lastUpdate) {
        Preconditions.checkNotNull(sentiment);
        Preconditions.checkState(!MockFactory.allUsers.containsKey(newUser.getUserId()));
        MockFactory.allUsers.put(newUser.getUserId(), newUser);
        MockFactory.sentimentByUser.put(newUser.getUserId(), sentiment);
        MockFactory.lastStatusUpdateByUser.put(newUser.getUserId(), lastUpdate);
    }

    @Override
    public User lookupUserById(final UUID userId) {
        Preconditions.checkNotNull(userId);

        return
            EntryStream.of(MockFactory.allUsers)
                .values()
                .collect(MoreCollectors.onlyOne(user -> user.getUserId().equals(userId)))
            .orElseThrow(() -> new IllegalStateException("User not found by id " + userId));
    }

    @Override
    public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
        final User user = lookupUserById(userId);
        user.setName(userSettings.getName());
        user.setStockAvatar(userSettings.getStockAvatar());
    }

    @Override
    public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
        final Group group = MockFactory.allGroups.get(groupId);
        Preconditions.checkNotNull(group);
        group.setGroupName(groupSettings.getGroupName());
    }

    @Override
    public Optional<Group> findGroupById(final UUID groupId) {
        Preconditions.checkNotNull(groupId);

        return EntryStream.of(MockFactory.allGroups)
            .values()
            .findAny(group -> group.getGroupId().equals(groupId));

    }

    @Override
    public Group startNewGroup(String groupName, String groupCode) {

        final Group newGroup = new Group(UUID.randomUUID());
        newGroup.setGroupName(groupName);
        newGroup.setGroupCode(groupCode);

        MockFactory.allGroups.put(newGroup.getGroupId(), newGroup);
        return newGroup;
    }

    @Override
    public void joinGroup(UUID groupId, UUID userId) {
//        Preconditions.checkState(MockFactory.allGroups.contains(groupName), "Group <%s> does not exist", groupName);

        MockFactory.groupByUserId.put(userId, groupId);
    }


    @Override
    public void leaveGroup(final UUID groupId, final UUID userId) {
        MockFactory.groupByUserId.remove(userId);
    }

    /**
     * return all users in same group except the requesting user
     */
    @Override
    public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
        return
        EntryStream.of(MockFactory.groupByUserId)
            .filterValues(gid -> gid.equals(groupId))
            .filterKeys(MockFactory.allUsers::containsKey)
                .filterKeys(otherUserId -> !otherUserId.equals(currentUserId))
                .keys()
                .map(MockFactory.allUsers::get)
                .collect(Collectors.toList());
    }

    @Override
    public Sentiment findSentimentByUserId(UUID userId) {

        final Sentiment sentiment = MockFactory.sentimentByUser.get(userId);
        Preconditions.checkNotNull(
            sentiment, "Lookup error on sentiment lookup for user %s", userId);
        return sentiment;
    }

    @Override
    public Instant findLastStatusUpdateByUserId(UUID userId) {
        final Instant lastStatusUpdate = MockFactory.lastStatusUpdateByUser.get(userId);
        Preconditions.checkNotNull(
                lastStatusUpdate, "Lookup error on last status update timestamp lookup for user %s", userId);
        return lastStatusUpdate;
    }


    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        lookupUserById(userId);
        MockFactory.sentimentByUser.put(userId, sentiment);
    }


    @Override
    public Optional<Group> findGroupByUser(final UUID userId) {
        return Optional.ofNullable(
            MockFactory.groupByUserId.get(userId))
                .map(MockFactory.allGroups::get);
    }

    @Override
    public Optional<Group> findGroupByCode(final String groupCode) {

        final List<Group> matches = EntryStream.of(MockFactory.allGroups)
                .filterValues(group -> group.getGroupCode().equals(groupCode))
                .values()
                .toList();

        Preconditions.checkState(matches.size() <= 1);

        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    @Override
    public Optional<Group> findGroupForUser(final UUID userId) {
        return Optional.ofNullable(MockFactory.groupByUserId.get(userId))
                .map(MockFactory.allGroups::get);
    }

    @Override
    public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return
                EntryStream.of(MockFactory.allUsers).values()
                        .findAny(user ->
                                user.getDeviceIdentifier()
                                        .equals(deviceIdentifier));
    }

    @Override
    public void touchLastStatusUpdate(final UUID userId) {
        MockFactory.lastStatusUpdateByUser.put(userId, Instant.now());
    }

}
