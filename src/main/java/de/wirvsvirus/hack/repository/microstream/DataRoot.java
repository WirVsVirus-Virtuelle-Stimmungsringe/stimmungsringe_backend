package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataRoot {

  private Map<UUID, User> allUsers;
  private Map<UUID, Group> allGroups;
  private Map<UUID, UUID> groupByUserId;
  private Map<UUID, Sentiment> sentimentByUser;
  private Map<UUID, Instant> lastStatusUpdateByUser;

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

  public Map<UUID, Sentiment> getSentimentByUser() {
    return sentimentByUser;
  }

  public void setSentimentByUser(
      Map<UUID, Sentiment> sentimentByUser) {
    this.sentimentByUser = sentimentByUser;
  }

  public Map<UUID, Instant> getLastStatusUpdateByUser() {
    return lastStatusUpdateByUser;
  }

  public void setLastStatusUpdateByUser(
      Map<UUID, Instant> lastStatusUpdateByUser) {
    this.lastStatusUpdateByUser = lastStatusUpdateByUser;
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
}
