package de.wirvsvirus.hack.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.service.OnboardingService;
import de.wirvsvirus.hack.service.dto.DeviceType;
import de.wirvsvirus.hack.spring.Database;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@SpringBootTest(properties = {"backend.microstream.storage-path=test-micro/"})
@ActiveProfiles("microstream")
public class PersistenceTest {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private OnboardingService onboardingService;

  @Autowired
  private Database database;

  private String deviceIdentifier = "deviceIdent-" + new Random().nextLong();
  private String groupCode = "groupCode-" + new Random().nextLong();

  @Test
  void addUsersJoinGroup() {
    final User newUser1;
    {
      newUser1 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser1.setRoles(Collections.emptyList());
      newUser1.setName("Flick");
      onboardingRepository.createNewUser(newUser1, Sentiment.sunnyWithClouds,
          "Wolken! Welche Wolken?", Instant.now());
    }
    final User newUser2;
    {
      newUser2 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser2.setRoles(Collections.emptyList());
      newUser2.setName("Flack");
      onboardingRepository.createNewUser(newUser2, Sentiment.sunnyWithClouds,
          "Wolken! Welche Wolken?", Instant.now());
    }

    final Device device = new Device();
    device.setUserId(newUser1.getUserId());
    device.setDeviceIdentifier(deviceIdentifier);
    device.setDeviceType(DeviceType.ANDROID);
    device.setFcmToken("fcm1212121212");
    onboardingRepository.addDevice(device);
    final List<Device> devLookup = onboardingRepository
        .findDevicesByUserId(newUser1.getUserId());

    final Group group = onboardingRepository.startNewGroup("Testgrp", groupCode);


    assertEquals("Testgrp", onboardingRepository.findGroupByCode(groupCode)
        .get().getGroupName());
    assertFalse(onboardingRepository.findGroupByCode("NoGroup")
        .isPresent());

    onboardingRepository.joinGroup(group.getGroupId(), newUser1.getUserId());
    onboardingRepository.joinGroup(group.getGroupId(), newUser2.getUserId());

    final List<User> others = onboardingRepository
        .findOtherUsersInGroup(group.getGroupId(), newUser1.getUserId());

    assertEquals(1, others.size());
    assertEquals("Flack", others.get(0).getName());

    onboardingRepository.leaveGroup(group.getGroupId(), newUser2.getUserId());

    assertFalse(onboardingRepository.findGroupByUser(newUser2.getUserId()).isPresent());

  }

  @Test
  void updateStatus() throws Exception {
    final User newUser1;
    {
      newUser1 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser1.setRoles(Collections.emptyList());
      newUser1.setName("Flick");
      onboardingRepository.createNewUser(newUser1, Sentiment.sunnyWithClouds,
          "Wolken! Welche Wolken?", Instant.now());
    }
    final Group group = onboardingRepository.startNewGroup("Testgrp", groupCode);

    onboardingRepository.joinGroup(group.getGroupId(), newUser1.getUserId());

    final Instant update1 = onboardingRepository
        .findLastStatusUpdateByUserId(newUser1.getUserId());

    Thread.sleep(1);

    onboardingRepository.updateStatus(newUser1.getUserId(), Sentiment.cloudy,
        "No money!");
    onboardingRepository.touchLastStatusUpdate(newUser1.getUserId());

    final Instant update2 = onboardingRepository
        .findLastStatusUpdateByUserId(newUser1.getUserId());

    assertEquals(Sentiment.cloudy,
        onboardingRepository.findSentimentByUserId(newUser1.getUserId()));

    Assertions.assertTrue(update2.isAfter(update1));
  }

  @Test
  void addDevice() {
    final User newUser1;
    {
      newUser1 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser1.setRoles(Collections.emptyList());
      newUser1.setName("Flick");
      onboardingRepository.createNewUser(newUser1, Sentiment.sunnyWithClouds,
          "Wolken! Welche Wolken?", Instant.now());
    }

    {
      final Device device1 = new Device();
      device1.setUserId(newUser1.getUserId());
      device1.setDeviceIdentifier(deviceIdentifier);
      device1.setDeviceType(DeviceType.ANDROID);
      device1.setFcmToken("fcm1212121212");
      onboardingRepository.addDevice(device1);

      assertEquals(1, onboardingRepository
          .findDevicesByUserId(newUser1.getUserId()).size());
      assertEquals(true, onboardingRepository
          .findByDeviceIdentifier(deviceIdentifier).isPresent());
    }

    {
      // WHEN adding same device with same FCM token -> skip
      final Device device2 = new Device();
      device2.setUserId(newUser1.getUserId());
      device2.setDeviceIdentifier(deviceIdentifier);
      device2.setDeviceType(DeviceType.ANDROID);
      device2.setFcmToken("fcm1212121212");
      onboardingRepository.addDevice(device2);

      // THEN should be ignored
      assertEquals(1, onboardingRepository
          .findDevicesByUserId(newUser1.getUserId()).size());
      assertEquals(true, onboardingRepository
          .findByDeviceIdentifier(deviceIdentifier).isPresent());
    }

    {
      // WHEN adding same device with different FCM token -> add
      final Device device2 = new Device();
      device2.setUserId(newUser1.getUserId());
      device2.setDeviceIdentifier(deviceIdentifier);
      device2.setDeviceType(DeviceType.ANDROID);
      device2.setFcmToken("fcm3333333");
      onboardingRepository.addDevice(device2);

      // THEN should be ignored
      assertEquals(2, onboardingRepository
          .findDevicesByUserId(newUser1.getUserId()).size());
      assertEquals(true, onboardingRepository
          .findByDeviceIdentifier(deviceIdentifier).isPresent());
    }
  }

  @Test
  void sendMessages() {
    final User alice;
    {
      alice = new User(UUID.randomUUID(), deviceIdentifier);
      alice.setRoles(Collections.emptyList());
      alice.setName("Alice");
      onboardingRepository.createNewUser(alice, Sentiment.sunnyWithClouds, "Wolken! Welche Wolken?",
          Instant.now());
    }
    final User bob;
    {
      bob = new User(UUID.randomUUID(), deviceIdentifier);
      bob.setRoles(Collections.emptyList());
      bob.setName("Bob");
      onboardingRepository.createNewUser(bob, Sentiment.sunnyWithClouds, "Wolken! Welche Wolken?",
          Instant.now());
    }

    final Group group = onboardingRepository.startNewGroup("Testgrp", groupCode);

    onboardingRepository.joinGroup(group.getGroupId(), alice.getUserId());
    onboardingRepository.joinGroup(group.getGroupId(), bob.getUserId());

    // WHEN
    onboardingRepository.sendMessage(alice, bob, "Hi Bob");
    onboardingRepository.sendMessage(alice, bob, "Hi Bob, again");

    // THEN
    assertEquals(2, onboardingRepository.findMessagesByRecipientId(bob.getUserId()).size());

    // WHEN
    onboardingRepository.clearMessagesByRecipientId(bob.getUserId());

    // THEN
    assertEquals(0, onboardingRepository.findMessagesByRecipientId(bob.getUserId()).size());

  }

}
