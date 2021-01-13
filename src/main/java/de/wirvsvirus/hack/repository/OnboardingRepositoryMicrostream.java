package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import de.wirvsvirus.hack.spring.Database;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import one.microstream.storage.types.StorageManager;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("microstream")
public class OnboardingRepositoryMicrostream implements OnboardingRepository {

  @Autowired
  private StorageManager storageManager;

  @Autowired
  private Database database;

  @Override
  public Optional<Group> findGroupById(final UUID groupId) {
    Preconditions.checkNotNull(groupId);
    return EntryStream.of(database.reloadRoot().getAllGroups())
        .values()
        .findAny(group -> group.getGroupId().equals(groupId));

  }

  @Override
  public void createNewUser(final User newUser, final Sentiment sentiment,
      final Instant lastUpdate) {
    Preconditions.checkNotNull(sentiment);
    Preconditions.checkState(!database.reloadRoot().getAllUsers().containsKey(newUser.getUserId()));
    database.reloadRoot().getAllUsers().put(newUser.getUserId(), newUser);
    database.reloadRoot().getSentimentByUser().put(newUser.getUserId(), sentiment);
    database.reloadRoot().getLastStatusUpdateByUser().put(newUser.getUserId(), lastUpdate);

    storageManager.store(database.reloadRoot().getAllUsers());
    storageManager.store(database.reloadRoot().getSentimentByUser());
    storageManager.store(database.reloadRoot().getLastStatusUpdateByUser());
  }

  @Override
  public User lookupUserById(final UUID userId) {
    Preconditions.checkNotNull(userId);

    return
        EntryStream.of(database.reloadRoot().getAllUsers())
            .values()
            .collect(MoreCollectors.onlyOne(user -> user.getUserId().equals(userId)))
            .orElseThrow(() -> new IllegalStateException("User not found by id " + userId));
  }

  @Override
  public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
    final User user = lookupUserById(userId);
    user.setName(userSettings.getName());
    user.setStockAvatar(userSettings.getStockAvatar());

    storageManager.store(user);
  }

  @Override
  public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
    final Group group = database.reloadRoot().getAllGroups().get(groupId);
    Preconditions.checkNotNull(group);
    group.setGroupName(groupSettings.getGroupName());

    storageManager.store(group);
  }

  @Override
  public Group startNewGroup(final String groupName, final String groupCode) {
    final Group newGroup = new Group(UUID.randomUUID());
    newGroup.setGroupName(groupName);
    newGroup.setGroupCode(groupCode);

    database.reloadRoot().getAllGroups().put(newGroup.getGroupId(), newGroup);
    database.reloadRoot().getAllGroupMessages().putIfAbsent(newGroup.getGroupId(), new ArrayList<>());

    storageManager.store(database.reloadRoot().getAllGroups());
    storageManager.store(database.reloadRoot().getAllGroupMessages());

    return newGroup;
  }


  @Override
  public void joinGroup(final UUID groupId, final UUID userId) {
    database.reloadRoot().getGroupByUserId().put(userId, groupId);

    storageManager.store(database.reloadRoot().getGroupByUserId());
  }

  @Override
  public void leaveGroup(final UUID groupId, final UUID userId) {
    database.reloadRoot().getGroupByUserId().remove(userId);

    storageManager.store(database.reloadRoot().getGroupByUserId());
  }

  @Override
  public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
    return
        EntryStream.of(database.reloadRoot().getGroupByUserId())
            .filterValues(gid -> gid.equals(groupId))
            .filterKeys(database.reloadRoot().getAllDevicesByUser()::containsKey)
            .filterKeys(otherUserId -> !otherUserId.equals(currentUserId))
            .keys()
            .map(database.reloadRoot().getAllUsers()::get)
            .collect(Collectors.toList());
  }

  @Override
  public Sentiment findSentimentByUserId(final UUID userId) {
    final Sentiment sentiment = database.reloadRoot().getSentimentByUser().get(userId);
    Preconditions.checkNotNull(
        sentiment, "Lookup error on sentiment lookup for user %s", userId);
    return sentiment;
  }

  @Override
  public Instant findLastStatusUpdateByUserId(final UUID userId) {
    final Instant lastStatusUpdate = database.reloadRoot().getLastStatusUpdateByUser().get(userId);
    Preconditions.checkNotNull(
        lastStatusUpdate, "Lookup error on last status update timestamp lookup for user %s", userId);
    return lastStatusUpdate;
  }

  @Override
  public void touchLastStatusUpdate(final UUID userId) {
    database.reloadRoot().getLastStatusUpdateByUser().put(userId, Instant.now());

    storageManager.store(database.reloadRoot().getLastStatusUpdateByUser());
  }

  @Override
  public void updateStatus(final UUID userId, final Sentiment sentiment) {
    lookupUserById(userId);
    database.reloadRoot().getSentimentByUser().put(userId, sentiment);

    storageManager.store(database.reloadRoot().getSentimentByUser());
  }

  @Override
  public Optional<Group> findGroupByUser(final UUID userId) {
    return Optional.ofNullable(
        database.reloadRoot().getGroupByUserId().get(userId))
        .map(database.reloadRoot().getAllGroups()::get);
  }

  @Override
  public Optional<Group> findGroupByCode(final String groupCode) {

    final List<Group> matches = EntryStream.of(database.reloadRoot().getAllGroups())
        .filterValues(group -> group.getGroupCode().equals(groupCode))
        .values()
        .toList();

    Preconditions.checkState(matches.size() <= 1);

    return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
  }

  @Override
  public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
    return
        EntryStream.of(database.reloadRoot().getAllUsers()).values()
            .findAny(user ->
                user.getDeviceIdentifier()
                    .equals(deviceIdentifier));
  }

  @Override
  public void sendMessage(final User sender, final User recipient, final String text) {
    final Group group1 = findGroupByUser(sender.getUserId()).orElseThrow(() -> new IllegalStateException("User not in any group"));

    final Message message = new Message();
    message.setGroupId(group1.getGroupId());
    message.setMessageId(UUID.randomUUID());
    message.setCreatedAt(Instant.now());
    message.setSenderUserId(sender.getUserId());
    message.setRecipientUserId(recipient.getUserId());
    message.setText(text);
    database.reloadRoot().getAllGroupMessages().get(group1.getGroupId()).add(message);

    storageManager.store(database.reloadRoot().getAllGroupMessages().get(group1.getGroupId()));
    // TODO try this instead
//    storageManager.store(database.reloadRoot().getAllGroupMessages());
  }

  @Override
  public List<Message> findMessagesByRecipientId(final UUID userId) {
    final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
    final List<Message> messageList = database.reloadRoot().getAllGroupMessages().get(group.getGroupId());
    Preconditions.checkNotNull(messageList);
    return messageList.stream()
        .filter(message -> message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public void clearMessagesByRecipientId(final UUID userId) {
    final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
    final List<Message> messageList = database.reloadRoot().getAllGroupMessages().get(group.getGroupId());
    Preconditions.checkNotNull(messageList);

    final List<Message> messagesWithoutOwn = messageList.stream()
        .filter(message -> !message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());

    database.reloadRoot().getAllGroupMessages().put(group.getGroupId(), messagesWithoutOwn);

    storageManager.store(database.reloadRoot().getAllGroupMessages());
  }

  @Override
  public void addDevice(final Device device) {
    Preconditions.checkNotNull(device.getUserId());
    Preconditions.checkNotNull(device.getDeviceIdentifier());
    Preconditions.checkNotNull(device.getDeviceType());
    Preconditions.checkNotNull(device.getFcmToken());

    database.reloadRoot().getAllDevicesByUser().putIfAbsent(device.getUserId(), new ArrayList<>());

    storageManager.store(database.reloadRoot().getAllDevicesByUser());

    final List<Device> devices = database.reloadRoot().getAllDevicesByUser().get(device.getUserId());

    final Optional<Device> existing = devices.stream()
        .collect(MoreCollectors.onlyOne(
            de -> de.getUserId().equals(device.getUserId())
                && de.getDeviceIdentifier().equals(device.getDeviceIdentifier())));

    if (!existing.isPresent()
        || !existing.get().getFcmToken().equals(device.getFcmToken())) {
      devices.add(device);
    }

    storageManager.store(existing);
  }

  @Override
  public List<Device> findDevicesByUserId(UUID userId) {
    return database.reloadRoot().getAllDevicesByUser().getOrDefault(userId, new ArrayList<>());
  }

  @Override
  public Stream<User> findAllUsers() {
    return database.reloadRoot().getAllUsers().values().stream();
  }
}
