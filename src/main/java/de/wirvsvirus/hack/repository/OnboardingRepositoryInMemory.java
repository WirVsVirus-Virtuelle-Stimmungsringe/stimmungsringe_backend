package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("!dynamodb")
public class OnboardingRepositoryInMemory implements OnboardingRepository {

    @PostConstruct
    public void initMock() {

        final Group rasselbande = startNewGroup("Rasselbande");
        joinGroup(rasselbande.getGroupId(), UUID.fromString("cafecafe-b855-46ba-b907-321d2d38beef"));
        joinGroup(rasselbande.getGroupId(), UUID.fromString("12340000-b855-46ba-b907-321d2d38feeb"));
        joinGroup(rasselbande.getGroupId(), UUID.fromString("deadbeef-b855-46ba-b907-321d01010101"));

        MockFactory.allUsers.values().forEach(user -> {
            final Sentiment sentiment = MockFactory.sentimentByUser(user.getUserId());
            updateStatus(user.getUserId(), sentiment);
        });

    }

    @Override
    public void createNewUser(final User newUser) {
        Preconditions.checkState(!MockFactory.allUsers.containsKey(newUser.getUserId()));
        MockFactory.allUsers.put(newUser.getUserId(), newUser);
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
    public Optional<Group> findGroupById(final UUID groupId) {
        Preconditions.checkNotNull(groupId);

        return EntryStream.of(MockFactory.allGroups)
            .values()
            .findAny(group -> group.getGroupId().equals(groupId));

    }

    @Override
    public Group startNewGroup(String groupName) {
        final Group newGroup = new Group(UUID.randomUUID());
        newGroup.setGroupName(groupName);

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
    public List<User> findOtherUsersInGroup(UUID userId) {
        return
            EntryStream.of(MockFactory.allUsers).values()
            .filter(user -> !user.getUserId().equals(userId))
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
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        MockFactory.sentimentByUser.put(userId, sentiment);
    }


    @Override
    public Optional<Group> findGroupByUser(final UUID userId) {
        return Optional.ofNullable(
            MockFactory.groupByUserId.get(userId))
                .map(MockFactory.allGroups::get);
    }

    @Override
    public Optional<Group> findGroupByName(final String groupName) {

        final Optional<Group> found = EntryStream.of(MockFactory.allGroups)
            .filterValues(group -> group.getGroupName().equals(groupName))
            .values()
            .findAny();

        return found;
    }

    @Override
    public Optional<Group> findGroupNameForUser(final UUID userId) {
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


}
