package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.model.Device;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.service.dto.DeviceType;
import de.wirvsvirus.hack.spring.Database;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import jdk.nashorn.internal.ir.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySources;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.AssertionErrors;

@SpringBootTest(properties = {"backend.microstream.storage-path=test-micro/"})
@ActiveProfiles("microstream")
public class PersistenceTest {


  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private Database database;

  private String deviceIdentifier = "deviceIdent-" + new Random().nextLong();
  private String groupCode = "groupCode-" + new Random().nextLong();

  @Test
  void name() {
    final User newUser1;
    {
      newUser1 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser1.setRoles(Collections.emptyList());
      newUser1.setName("Flick");
      onboardingRepository.createNewUser(newUser1, Sentiment.sunnyWithClouds, Instant.now());
    }
    final User newUser2;
    {
      newUser2 = new User(UUID.randomUUID(), deviceIdentifier);
      newUser2.setRoles(Collections.emptyList());
      newUser2.setName("Flack");
      onboardingRepository.createNewUser(newUser2, Sentiment.sunnyWithClouds, Instant.now());
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

    onboardingRepository.joinGroup(group.getGroupId(), newUser1.getUserId());
    onboardingRepository.joinGroup(group.getGroupId(), newUser2.getUserId());

    final List<User> others = onboardingRepository
        .findOtherUsersInGroup(group.getGroupId(), newUser1.getUserId());

    final DataRoot dataRoot = database.dataRoot();

    System.out.println("data");
    AssertionErrors.assertEquals("must be in group", 1, others.size());
    AssertionErrors.assertEquals("must be in group", "Flack", others.get(0).getName());

    System.out.println("others: ");
    others.forEach(ou -> {
      System.out.println("- " + ou);
    });


  }
}
