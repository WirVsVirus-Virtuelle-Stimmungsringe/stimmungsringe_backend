package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.AchievementShownStatus;
import de.wirvsvirus.hack.model.AchievementType;
import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserStatus;
import de.wirvsvirus.hack.model.UserStatusChangeHistory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * on change .. consider updating deleteUser
 */
public class DataRoot {

  private Map<UUID, User> allUsers;
  private Map<UUID, Group> allGroups;
  private Map<UUID, UUID> groupByUserId;
  @Deprecated
  private Map<UUID, Sentiment> sentimentByUser;
  @Deprecated
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

  private List<UserStatusChangeHistory> historyUserStatusChanges;

  private List<UserGroupMembershipHistory> historyUserGroupMembership;

//  private Map<UUID, AchievementShownStatus> achievementShownStatusByUser;
  private Map<UUID, Map<AchievementType, AchievementShownStatus>> achievementShownStatusByUserAndType;

  //

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

  // userId -> groupId
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

  public List<UserStatusChangeHistory> getHistoryUserStatusChanges() {
    return historyUserStatusChanges;
  }

  public void setHistoryUserStatusChanges(
      List<UserStatusChangeHistory> historyUserStatusChanges) {
    this.historyUserStatusChanges = historyUserStatusChanges;
  }

  public List<UserGroupMembershipHistory> getHistoryUserGroupMembership() {
    return historyUserGroupMembership;
  }

  public void setHistoryUserGroupMembership(
      List<UserGroupMembershipHistory> historyUserGroupMembership) {
    this.historyUserGroupMembership = historyUserGroupMembership;
  }

  public Map<UUID, Map<AchievementType, AchievementShownStatus>> getAchievementShownStatusByUserAndType() {
    return achievementShownStatusByUserAndType;
  }

  public void setAchievementShownStatusByUserAndType(
      Map<UUID, Map<AchievementType, AchievementShownStatus>> achievementShownStatusByUserAndType) {
    this.achievementShownStatusByUserAndType = achievementShownStatusByUserAndType;
  }

  public void dumpToSysout() {
    System.out.println("-- Inspecting database:");

    System.out.println("   Groups");
    for (Group group : allGroups.values()) {
      System.out.println("   - G " + group.getGroupName() + " " + group.getGroupCode());
    }

    System.out.println("   Users");
    for (User user : allUsers.values()) {
      System.out.println("   - M " + user.getName() + " " + user.getUserId());
    }

  }
}
