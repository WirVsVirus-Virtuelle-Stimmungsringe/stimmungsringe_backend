package de.wirvsvirus.hack.repository;

import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.InMemoryDatastore;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.spring.MicrostreamConfiguration;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import one.microstream.storage.types.StorageEntity;
import one.microstream.storage.types.StorageManager;
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
  private StorageManager storageManager;

  @Autowired
  private DataRoot dataRoot;

  @Override
  public Optional<Group> findGroupById(final UUID groupId) {
    Preconditions.checkNotNull(groupId);
    return EntryStream.of(dataRoot.getAllGroups())
        .values()
        .findAny(group -> group.getGroupId().equals(groupId));

  }

  @Override
  public void createNewUser(final User newUser, final Sentiment sentiment,
      final Instant lastUpdate) {
    Preconditions.checkNotNull(sentiment);
    Preconditions.checkState(!dataRoot.getAllUsers().containsKey(newUser.getUserId()));
    dataRoot.getAllUsers().put(newUser.getUserId(), newUser);
    dataRoot.getSentimentByUser().put(newUser.getUserId(), sentiment);
    dataRoot.getLastStatusUpdateByUser().put(newUser.getUserId(), lastUpdate);

    storageManager.store(dataRoot.getAllUsers());
    storageManager.store(dataRoot.getSentimentByUser());
    storageManager.store(dataRoot.getLastStatusUpdateByUser());
  }

  @Override
  public User lookupUserById(final UUID userId) {
    Preconditions.checkNotNull(userId);

    return
        EntryStream.of(dataRoot.getAllUsers())
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
    final Group group = InMemoryDatastore.allGroups.get(groupId);
    Preconditions.checkNotNull(group);
    group.setGroupName(groupSettings.getGroupName());

    storageManager.store(group);
  }

  @Override
  public Group startNewGroup(final String groupName, final String groupCode) {
    final Group newGroup = new Group(UUID.randomUUID());
    newGroup.setGroupName(groupName);
    newGroup.setGroupCode(groupCode);

    dataRoot.getAllGroups().put(newGroup.getGroupId(), newGroup);
    dataRoot.getAllGroupMessages().putIfAbsent(newGroup.getGroupId(), new ArrayList<>());

    storageManager.store(dataRoot.getAllGroups());
    storageManager.store(dataRoot.getAllGroupMessages());

    return newGroup;
  }


  @Override
  public void joinGroup(final UUID groupId, final UUID userId) {
    dataRoot.getGroupByUserId().put(userId, groupId);

    storageManager.store(dataRoot.getGroupByUserId());
  }

  @Override
  public void leaveGroup(final UUID groupId, final UUID userId) {
    dataRoot.getGroupByUserId().remove(userId);

    storageManager.store(dataRoot.getGroupByUserId());
  }

  @Override
  public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
    return
        EntryStream.of(dataRoot.getGroupByUserId())
            .filterValues(gid -> gid.equals(groupId))
            .filterKeys(dataRoot.getAllDevicesByUser()::containsKey)
            .filterKeys(otherUserId -> !otherUserId.equals(currentUserId))
            .keys()
            .map(dataRoot.getAllUsers()::get)
            .collect(Collectors.toList());
  }

  @Override
  public Sentiment findSentimentByUserId(final UUID userId) {
    final Sentiment sentiment = dataRoot.getSentimentByUser().get(userId);
    Preconditions.checkNotNull(
        sentiment, "Lookup error on sentiment lookup for user %s", userId);
    return sentiment;
  }

  @Override
  public Instant findLastStatusUpdateByUserId(final UUID userId) {
    final Instant lastStatusUpdate = dataRoot.getLastStatusUpdateByUser().get(userId);
    Preconditions.checkNotNull(
        lastStatusUpdate, "Lookup error on last status update timestamp lookup for user %s", userId);
    return lastStatusUpdate;
  }

  @Override
  public void touchLastStatusUpdate(final UUID userId) {
    dataRoot.getLastStatusUpdateByUser().put(userId, Instant.now());

    storageManager.store(dataRoot.getLastStatusUpdateByUser());
  }

  @Override
  public void updateStatus(final UUID userId, final Sentiment sentiment) {
    lookupUserById(userId);
    dataRoot.getSentimentByUser().put(userId, sentiment);

    storageManager.store(dataRoot.getSentimentByUser());
  }

  @Override
  public Optional<Group> findGroupByUser(final UUID userId) {
    return Optional.ofNullable(
        dataRoot.getGroupByUserId().get(userId))
        .map(dataRoot.getAllGroups()::get);
  }

  @Override
  public Optional<Group> findGroupByCode(final String groupCode) {

    final List<Group> matches = EntryStream.of(dataRoot.getAllGroups())
        .filterValues(group -> group.getGroupCode().equals(groupCode))
        .values()
        .toList();

    Preconditions.checkState(matches.size() <= 1);

    return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
  }

  @Override
  public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
    return
        EntryStream.of(dataRoot.getAllUsers()).values()
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
    dataRoot.getAllGroupMessages().get(group1.getGroupId()).add(message);

    storageManager.store(dataRoot.getAllGroupMessages().get(group1.getGroupId()));
    // TODO try this instead
//    storageManager.store(dataRoot.getAllGroupMessages());
  }

  @Override
  public List<Message> findMessagesByRecipientId(final UUID userId) {
    final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
    final List<Message> messageList = dataRoot.getAllGroupMessages().get(group.getGroupId());
    Preconditions.checkNotNull(messageList);
    return messageList.stream()
        .filter(message -> message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public void clearMessagesByRecipientId(final UUID userId) {
    final Group group = findGroupByUser(userId).orElseThrow(() -> new IllegalStateException("User not member of any group"));
    final List<Message> messageList = dataRoot.getAllGroupMessages().get(group.getGroupId());
    Preconditions.checkNotNull(messageList);

    final List<Message> messagesWithoutOwn = messageList.stream()
        .filter(message -> !message.getRecipientUserId().equals(userId))
        .collect(Collectors.toList());

    dataRoot.getAllGroupMessages().put(group.getGroupId(), messagesWithoutOwn);

    storageManager.store(dataRoot.getAllGroupMessages());
  }

  @Override
  public void addDevice(final Device device) {
    Preconditions.checkNotNull(device.getUserId());
    Preconditions.checkNotNull(device.getDeviceIdentifier());
    Preconditions.checkNotNull(device.getDeviceType());
    Preconditions.checkNotNull(device.getFcmToken());

    dataRoot.getAllDevicesByUser().putIfAbsent(device.getUserId(), new ArrayList<>());

    storageManager.store(dataRoot.getAllDevicesByUser());

    final List<Device> devices = dataRoot.getAllDevicesByUser().get(device.getUserId());

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
    return dataRoot.getAllDevicesByUser().getOrDefault(userId, new ArrayList<>());
  }

  @Override
  public Stream<User> findAllUsers() {
    return dataRoot.getAllUsers().values().stream();
  }
}
