package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataRoot {

  private Map<UUID, User> allUsers;
  private Map<UUID, Group> allGroups;
  private Map<UUID, UUID> groupByUserId;
  private Map<UUID, Sentiment> sentimentByUser;
  private Map<UUID, Instant> lastStatusUpdateByUser;
  private Map<UUID, UserStatus> statusByUser;

  private MigrationMetadata migrationMetadata;

  /**
   * groupId -> list message
   */
  private Map<UUID, List<Message>> allGroupMessages;

  /**
   * userId -> list devices
   */
  private Map<UUID, List<Device>> allDevicesByUser;


  public Map<UUID, User> getAllUsers() {
    return allUsers;
  }

  public void setAllUsers(Map<UUID, User> allUsers) {
    this.allUsers = allUsers;
  }

  public Map<UUID, Group> getAllGroups() {
    return allGroups;
  }

  public void setAllGroups(Map<UUID, Group> allGroups) {
    this.allGroups = allGroups;
  }

  public Map<UUID, UUID> getGroupByUserId() {
    return groupByUserId;
  }

  public void setGroupByUserId(Map<UUID, UUID> groupByUserId) {
    this.groupByUserId = groupByUserId;
  }

  public Map<UUID, UserStatus> getStatusByUser() {
    return statusByUser;
  }

  public void setStatusByUser(
      Map<UUID, UserStatus> statusByUser) {
    this.statusByUser = statusByUser;
  }

  public Map<UUID, List<Message>> getAllGroupMessages() {
    return allGroupMessages;
  }

  public void setAllGroupMessages(
      Map<UUID, List<Message>> allGroupMessages) {
    this.allGroupMessages = allGroupMessages;
  }

  public Map<UUID, List<Device>> getAllDevicesByUser() {
    return allDevicesByUser;
  }

  public void setAllDevicesByUser(
      Map<UUID, List<Device>> allDevicesByUser) {
    this.allDevicesByUser = allDevicesByUser;
  }

  public MigrationMetadata getMigrationMetadata() {
    return migrationMetadata;
  }

  public void setMigrationMetadata(
      MigrationMetadata migrationMetadata) {
    this.migrationMetadata = migrationMetadata;
  }

  public void dumpToSysout() {
    System.out.println("-- Inspecting database:");

    System.out.println("   Users");
    for (User user : allUsers.values()) {
      System.out.println("   - M " + user.getName() + " " + user.getUserId());
    }
  }
}
