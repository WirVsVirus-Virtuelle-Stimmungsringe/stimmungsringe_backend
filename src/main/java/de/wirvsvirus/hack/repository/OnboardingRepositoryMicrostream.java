package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserStatus;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import de.wirvsvirus.hack.spring.Database;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("microstream")
public class OnboardingRepositoryMicrostream implements OnboardingRepository {

  @Autowired
  private Database database;

  @Override
  public Optional<Group> findGroupById(final UUID groupId) {
    Preconditions.checkNotNull(groupId);
    return EntryStream.of(database.dataRoot().getAllGroups())
        .values()
        .findAny(group -> group.getGroupId().equals(groupId));

  }

  @Override
  public void createNewUser(final User newUser, final Sentiment sentiment,
      final String sentimentText, final Instant lastUpdate) {
    Preconditions.checkNotNull(sentiment);
    Preconditions.checkState(!database.dataRoot().getAllUsers().containsKey(newUser.getUserId()));
    database.dataRoot().getAllUsers().put(newUser.getUserId(), newUser);
    final UserStatus userStatus = new UserStatus();
    userStatus.setSentiment(sentiment);
    userStatus.setSentimentText(sentimentText);
    userStatus.setLastStatusUpdate(lastUpdate);

    database.dataRoot().getStatusByUser().put(newUser.getUserId(), userStatus);
    database.persist(database.dataRoot().getStatusByUser());

    database.persist(database.dataRoot().getAllUsers());
  }

  @Override
  public User lookupUserById(final UUID userId) {
    Preconditions.checkNotNull(userId);

    return
        EntryStream.of(database.dataRoot().getAllUsers())
            .values()
            .collect(MoreCollectors.onlyOne(user -> user.getUserId().equals(userId)))
            .orElseThrow(() -> new IllegalStateException("Unique user not found by id " + userId));
  }

  @Override
  public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
    final User user = lookupUserById(userId);
    user.setName(userSettings.getName());
    user.setStockAvatar(userSettings.getStockAvatar());

    database.persist(user);
  }

  @Override
  public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
    final Group group = database.dataRoot().getAllGroups().get(groupId);
    Preconditions.checkNotNull(group);
    group.setGroupName(groupSettings.getGroupName());

    database.persist(group);
  }

  @Override
  public Group startNewGroup(final String groupName, final String groupCode) {
    final Group newGroup = new Group(UUID.randomUUID());
    newGroup.setGroupName(groupName);
    newGroup.setGroupCode(groupCode);

    database.dataRoot().getAllGroups().put(newGroup.getGroupId(), newGroup);
    database.dataRoot().getAllGroupMessages().putIfAbsent(newGroup.getGroupId(), new ArrayList<>());

    database.persist(database.dataRoot().getAllGroups());
    database.persist(database.dataRoot().getAllGroupMessages());

    return newGroup;
  }


  @Override
  public void joinGroup(final UUID groupId, final UUID userId) {
    database.dataRoot().getGroupByUserId().put(userId, groupId);

    database.persist(database.dataRoot().getGroupByUserId());
  }

  @Override
  public void leaveGroup(final UUID groupId, final UUID userId) {
    database.dataRoot().getGroupByUserId().remove(userId);

    database.persist(database.dataRoot().getGroupByUserId());
  }

  @Override
  public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
    return
        EntryStream.of(database.dataRoot().getGroupByUserId())
            .filterValues(gid -> gid.equals(groupId))
            .filterKeys(otherUserId -> !otherUserId.equals(currentUserId))
            .keys()
            .map(database.dataRoot().getAllUsers()::get)
            .collect(Collectors.toList());
  }

  @Override
  public Sentiment findSentimentByUserId(final UUID userId) {
    final Sentiment sentiment = database.dataRoot().getStatusByUser()
        .get(userId).getSentiment();
    Preconditions.checkNotNull(
        sentiment, "Lookup error on sentiment lookup for user %s", userId);
    return sentiment;
  }

  @Override
  public String findSentimentTextByUserId(UUID userId) {
    final String sentimentText = database.dataRoot().getStatusByUser()
        .get(userId).getSentimentText();
    Preconditions.checkNotNull(
        sentimentText, "Lookup error on sentiment text lookup for user %s", userId);
    return sentimentText;
  }

  @Override
  public Instant findLastStatusUpdateByUserId(final UUID userId) {
    final Instant lastStatusUpdate = database.dataRoot().getStatusByUser()
        .get(userId).getLastStatusUpdate();
    Preconditions.checkNotNull(
        lastStatusUpdate,
        "Lookup error on last status update timestamp lookup for user %s", userId);
    return lastStatusUpdate;
  }

  @Override
  public Instant findLastSigninByUserId(UUID userId) {
    final Instant lastSignin = database.dataRoot().getStatusByUser()
        .get(userId).getLastSignin();
    Preconditions.checkNotNull(
        lastSignin,
        "Lookup error on last signin timestamp lookup for user %s", userId);
    return lastSignin;
  }

  @Override
  public void touchLastStatusUpdate(final UUID userId) {
    final UserStatus userStatus = database.dataRoot().getStatusByUser().get(userId);
    userStatus.setLastStatusUpdate(Instant.now());

    database.persist(userStatus);
  }

  @Override
  public void touchLastSignin(final UUID userId) {
    final UserStatus userStatus = database.dataRoot().getStatusByUser().get(userId);
    userStatus.setLastSignin(Instant.now());

    database.persist(userStatus);
  }

  @Override
  public void updateStatus(final UUID userId,
      final Sentiment sentiment,
      final String sentimentText) {
    lookupUserById(userId);

    final UserStatus userStatus = database.dataRoot().getStatusByUser().get(userId);
    userStatus.setSentiment(sentiment);
    userStatus.setSentimentText(sentimentText);

    database.persist(userStatus);
  }

  @Override
  public Optional<Group> findGroupByUser(final UUID userId) {
    return Optional.ofNullable(
        database.dataRoot().getGroupByUserId().get(userId))
        .map(database.dataRoot().getAllGroups()::get);
  }

  @Override
  public Optional<Group> findGroupByCode(final String groupCode) {

    final List<Group> matches = EntryStream.of(database.dataRoot().getAllGroups())
        .filterValues(group -> group.getGroupCode().equals(groupCode))
        .values()
        .toList();

    Preconditions.checkState(matches.size() <= 1);

    return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
  }

  @Override
  public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
    return
        EntryStream.of(database.dataRoot().getAllUsers()).values()
            .findAny(user ->
                user.getDeviceIdentifier()
                    .equals(deviceIdentifier));
  }

  @Override
  public void sendMessage(final User sender, final User recipient, final String text) {
    final Group group1 = findGroupByUser(sender.getUserId()).orElseThrow(() -> new IllegalStateException("User not in any group"));

    final List<Message> messages = database.dataRoot().getAllGroupMessages()
        .get(group1.getGroupId());

    final Message message = new Message();
    message.setGroupId(group1.getGroupId());
    message.setMessageId(UUID.randomUUID());
    message.setCreatedAt(Instant.now());
    message.setSenderUserId(sender.getUserId());
    message.setRecipientUserId(recipient.getUserId());
    message.setText(text);

    messages.add(message);

    database.persist(messages);
  }

  @Override
  public List<Message> findMessagesByRecipientId(final UUID userId) {
    final Optional<Group> group = findGroupByUser(userId);
    if (!group.isPresent()) {
      // rare (racy) edge-case
      return Collections.emptyList();
    }

    final List<Message> messageList = database.dataRoot().getAllGroupMessages().get(group.get().getGroupId());
    Preconditions.checkNotNull(messageList);
    return messageList.stream()
        .filter(message -> message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public void clearMessagesByRecipientId(final UUID userId) {
    final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
    final List<Message> messageList = database.dataRoot().getAllGroupMessages().get(group.getGroupId());
    Preconditions.checkNotNull(messageList);

    final List<Message> messagesWithoutOwn = messageList.stream()
        .filter(message -> !message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());

    database.dataRoot().getAllGroupMessages().put(group.getGroupId(), messagesWithoutOwn);

    database.persist(database.dataRoot().getAllGroupMessages());
  }

  @Override
  public void addDevice(final Device device) {
    Preconditions.checkNotNull(device.getUserId());
    Preconditions.checkNotNull(device.getDeviceIdentifier());
    Preconditions.checkNotNull(device.getDeviceType());
    Preconditions.checkNotNull(device.getFcmToken());

    database.dataRoot().getAllDevicesByUser().putIfAbsent(device.getUserId(), new ArrayList<>());

    final List<Device> devices = database.dataRoot().getAllDevicesByUser().get(device.getUserId());

    if (devices.stream().anyMatch(
        d2 -> d2.getDeviceIdentifier().equals(device.getDeviceIdentifier())
        && d2.getFcmToken().equals(device.getFcmToken())
    )) {
      // duplicate
      return;
    }

    devices.add(device);
    database.persist(database.dataRoot().getAllDevicesByUser());
    database.persist(devices);
  }

  @Override
  public List<Device> findDevicesByUserId(UUID userId) {
    return database.dataRoot().getAllDevicesByUser().getOrDefault(userId, new ArrayList<>());
  }

  @Override
  public Stream<User> findAllUsers() {
    return database.dataRoot().getAllUsers().values().stream();
  }
}
