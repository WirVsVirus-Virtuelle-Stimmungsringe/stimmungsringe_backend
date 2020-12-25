package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.microstream.Microstream;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("microstream")
public class OnboardingRepositoryMicrostream implements OnboardingRepository {

  private OnboardingRepositoryInMemory memory;

  @PostConstruct
  public void startup() {

    memory = new OnboardingRepositoryInMemory();
//    Microstream.restoreFromStorage();
    Microstream.inspect();

  }


  private void markForFlush() {
    System.out.println("Flushing ..");
    Microstream.writeDataToStorage();
  }

  @Override
  public Optional<Group> findGroupById(final UUID groupId) {
    return memory.findGroupById(groupId);
  }

  @Override
  public void createNewUser(final User newUser, final Sentiment sentiment,
      final Instant lastUpdate) {
    memory.createNewUser(newUser, sentiment, lastUpdate);
    markForFlush();
  }

  @Override
  public User lookupUserById(final UUID userId) {
    return memory.lookupUserById(userId);
  }

  @Override
  public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
    memory.updateUser(userId, userSettings);
    markForFlush();
  }

  @Override
  public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
    memory.updateGroup(groupId, groupSettings);
    markForFlush();
  }

  @Override
  public Group startNewGroup(final String groupName, final String groupCode) {
    final Group group = memory.startNewGroup(groupName, groupCode);
    markForFlush();
    return group;
  }


  @Override
  public void joinGroup(final UUID groupId, final UUID userId) {
    memory.joinGroup(groupId, userId);
    markForFlush();
  }

  @Override
  public void leaveGroup(final UUID groupId, final UUID userId) {
    memory.leaveGroup(groupId, userId);
    markForFlush();
  }

  @Override
  public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
    return memory.findOtherUsersInGroup(groupId, currentUserId);
  }

  @Override
  public Sentiment findSentimentByUserId(final UUID userId) {
    return memory.findSentimentByUserId(userId);
  }

  @Override
  public Instant findLastStatusUpdateByUserId(final UUID userId) {
    return memory.findLastStatusUpdateByUserId(userId);
  }

  @Override
  public void touchLastStatusUpdate(final UUID userId) {
    memory.touchLastStatusUpdate(userId);
    markForFlush();
  }

  @Override
  public void updateStatus(final UUID userId, final Sentiment sentiment) {
    memory.updateStatus(userId, sentiment);
    markForFlush();
  }

  @Override
  public Optional<Group> findGroupByUser(final UUID userId) {
    return memory.findGroupByUser(userId);
  }

  @Override
  public Optional<Group> findGroupByCode(final String groupCode) {
    return memory.findGroupByCode(groupCode);
  }

  @Override
  public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
    return memory.findByDeviceIdentifier(deviceIdentifier);
  }

  @Override
  public void sendMessage(final User sender, final User recipient, final String text) {
    memory.sendMessage(sender, recipient, text);
    markForFlush();
  }

  @Override
  public List<Message> findMessagesByRecipientId(final UUID userId) {
    return memory.findMessagesByRecipientId(userId);
  }

  @Override
  public void clearMessagesByRecipientId(final UUID userId) {
    memory.clearMessagesByRecipientId(userId);
    markForFlush();
  }

  @Override
  public void addDevice(final Device device) {
    memory.addDevice(device);
    markForFlush();
  }

  @Override
  public List<Device> findDevicesByUserId(UUID userId) {
    return memory.findDevicesByUserId(userId);
  }

  @Override
  public Stream<User> findAllUsers() {
    return memory.findAllUsers();
  }
}
