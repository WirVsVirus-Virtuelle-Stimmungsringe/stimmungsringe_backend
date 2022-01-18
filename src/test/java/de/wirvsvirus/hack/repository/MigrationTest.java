package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.Application;
import de.wirvsvirus.hack.repository.PersistenceTest.PersistenceTestConfiguration;
import de.wirvsvirus.hack.spring.DatabaseMigration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@SpringBootTest(
    classes = {Application.class, PersistenceTestConfiguration.class},
    properties = {"backend.microstream.storage-path=file:${user.home}/familiarise-microstream-migrationtest/19/"})
@ActiveProfiles({"microstream", "no-push-notification-service"})
public class MigrationTest {

  @Autowired
  private DatabaseMigration databaseMigration;

  @Test
  void runMigration() {

    System.out.println();

  }
}
