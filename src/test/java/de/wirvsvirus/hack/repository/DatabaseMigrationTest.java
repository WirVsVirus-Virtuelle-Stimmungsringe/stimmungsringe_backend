package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.Application;
import de.wirvsvirus.hack.repository.PersistenceTest.PersistenceTestConfiguration;
import de.wirvsvirus.hack.spring.Database;
import de.wirvsvirus.hack.spring.DatabaseMigration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@SpringBootTest(
    classes = {Application.class, PersistenceTestConfiguration.class},
    properties = {"backend.microstream.storage-path=file:${user.home}/familiarise-microstream-migrationtest/"})
@ActiveProfiles({"microstream", "database-migration-test", "no-push-notification-service"})
public class DatabaseMigrationTest {

  @Autowired
  private DatabaseMigration databaseMigration;

  @Autowired
  private Database database;

  @Test
  void runMigrationOfGroups() {

    databaseMigration.runMigrations();

    database.dataRoot().getAllGroups().values()
            .forEach(group -> {
              System.out.println("- " + group);
            });

  }
}
