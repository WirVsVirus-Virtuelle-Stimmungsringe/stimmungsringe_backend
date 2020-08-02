package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.InMemoryDatastore;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
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

        joinGroup(rasselbande.getGroupId(), InMemoryDatastore.daniela.getUserId());
        touchLastStatusUpdate(InMemoryDatastore.daniela.getUserId());
        joinGroup(rasselbande.getGroupId(), InMemoryDatastore.frida.getUserId());
        touchLastStatusUpdate(InMemoryDatastore.frida.getUserId());
        joinGroup(rasselbande.getGroupId(), InMemoryDatastore.otto.getUserId());
        touchLastStatusUpdate(InMemoryDatastore.otto.getUserId());


        log.info("Created mock group " + rasselbande);

        sendMessage(InMemoryDatastore.frida, InMemoryDatastore.otto, "Hallo, Otto!");
        sendMessage(InMemoryDatastore.frida, InMemoryDatastore.daniela, "Ich denk' an dich!");
        sendMessage(InMemoryDatastore.daniela, InMemoryDatastore.frida, "Ich denk' an dich!");
        sendMessage(InMemoryDatastore.frida, InMemoryDatastore.otto, "Ich denk' an dich!");
    }

    @Override
    public void createNewUser(final User newUser, Sentiment sentiment, final Instant lastUpdate) {
        Preconditions.checkNotNull(sentiment);
        Preconditions.checkState(!InMemoryDatastore.allUsers.containsKey(newUser.getUserId()));
        InMemoryDatastore.allUsers.put(newUser.getUserId(), newUser);
        InMemoryDatastore.sentimentByUser.put(newUser.getUserId(), sentiment);
        InMemoryDatastore.lastStatusUpdateByUser.put(newUser.getUserId(), lastUpdate);
    }

    @Override
    public User lookupUserById(final UUID userId) {
        Preconditions.checkNotNull(userId);

        return
                EntryStream.of(InMemoryDatastore.allUsers)
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
        final Group group = InMemoryDatastore.allGroups.get(groupId);
        Preconditions.checkNotNull(group);
        group.setGroupName(groupSettings.getGroupName());
    }

    @Override
    public Optional<Group> findGroupById(final UUID groupId) {
        Preconditions.checkNotNull(groupId);

        return EntryStream.of(InMemoryDatastore.allGroups)
                .values()
                .findAny(group -> group.getGroupId().equals(groupId));

    }

    @Override
    public Group startNewGroup(String groupName, String groupCode) {

        final Group newGroup = new Group(UUID.randomUUID());
        newGroup.setGroupName(groupName);
        newGroup.setGroupCode(groupCode);

        InMemoryDatastore.allGroups.put(newGroup.getGroupId(), newGroup);
        InMemoryDatastore.allGroupMessages.putIfAbsent(newGroup.getGroupId(), new ArrayList<>());
        return newGroup;
    }

    @Override
    public void joinGroup(UUID groupId, UUID userId) {
//        Preconditions.checkState(MockFactory.allGroups.contains(groupName), "Group <%s> does not exist", groupName);

        InMemoryDatastore.groupByUserId.put(userId, groupId);
    }


    @Override
    public void leaveGroup(final UUID groupId, final UUID userId) {
        InMemoryDatastore.groupByUserId.remove(userId);
    }

    /**
     * return all users in same group except the requesting user
     */
    @Override
    public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
        return
                EntryStream.of(InMemoryDatastore.groupByUserId)
                        .filterValues(gid -> gid.equals(groupId))
                        .filterKeys(InMemoryDatastore.allUsers::containsKey)
                        .filterKeys(otherUserId -> !otherUserId.equals(currentUserId))
                        .keys()
                        .map(InMemoryDatastore.allUsers::get)
                        .collect(Collectors.toList());
    }

    @Override
    public Sentiment findSentimentByUserId(UUID userId) {

        final Sentiment sentiment = InMemoryDatastore.sentimentByUser.get(userId);
        Preconditions.checkNotNull(
                sentiment, "Lookup error on sentiment lookup for user %s", userId);
        return sentiment;
    }

    @Override
    public Instant findLastStatusUpdateByUserId(UUID userId) {
        final Instant lastStatusUpdate = InMemoryDatastore.lastStatusUpdateByUser.get(userId);
        Preconditions.checkNotNull(
                lastStatusUpdate, "Lookup error on last status update timestamp lookup for user %s", userId);
        return lastStatusUpdate;
    }


    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        lookupUserById(userId);
        InMemoryDatastore.sentimentByUser.put(userId, sentiment);
    }


    @Override
    public Optional<Group> findGroupByUser(final UUID userId) {
        return Optional.ofNullable(
                InMemoryDatastore.groupByUserId.get(userId))
                .map(InMemoryDatastore.allGroups::get);
    }

    @Override
    public Optional<Group> findGroupByCode(final String groupCode) {

        final List<Group> matches = EntryStream.of(InMemoryDatastore.allGroups)
                .filterValues(group -> group.getGroupCode().equals(groupCode))
                .values()
                .toList();

        Preconditions.checkState(matches.size() <= 1);

        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    @Override
    public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return
                EntryStream.of(InMemoryDatastore.allUsers).values()
                        .findAny(user ->
                                user.getDeviceIdentifier()
                                        .equals(deviceIdentifier));
    }

    @Override
    public void touchLastStatusUpdate(final UUID userId) {
        InMemoryDatastore.lastStatusUpdateByUser.put(userId, Instant.now());
    }

    @Override
    public void sendMessage(final User sender, final User recipient, final String text) {
        final Group group1 = findGroupByUser(sender.getUserId()).orElseThrow(() -> new IllegalStateException("User not in any group"));

        final Message message = new Message();
        message.setCreatedAt(Instant.now());
        message.setSenderUserId(sender.getUserId());
        message.setRecipientUserId(recipient.getUserId());
        message.setText(text);
        InMemoryDatastore.allGroupMessages.get(group1.getGroupId()).add(message);
    }

    @Override
    public List<Message> findMessagesByRecipientId(final UUID userId) {
        final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
        final List<Message> messageList = InMemoryDatastore.allGroupMessages.get(group.getGroupId());
        Preconditions.checkNotNull(messageList);
        return messageList.stream()
                .filter(message -> message.getRecipientUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void clearMessagesByRecipientId(final UUID userId) {
        final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
        final List<Message> messageList = InMemoryDatastore.allGroupMessages.get(group.getGroupId());
        Preconditions.checkNotNull(messageList);

        final List<Message> messagesWithoutOwn = messageList.stream()
                .filter(message -> !message.getRecipientUserId().equals(userId))
                .collect(Collectors.toList());

        InMemoryDatastore.allGroupMessages.put(group.getGroupId(), messagesWithoutOwn);
    }


}
