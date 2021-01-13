package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.mock.MockDataProvider;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.repository.microstream.DataRoot;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import javax.annotation.PostConstruct;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// run after repositories
@Service
public class DatabaseMigration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigration.class);

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private Database database;

  @PostConstruct
  public void runMigrations() {
    final MigrationMetadata migrationMetadata = database.reloadRoot().getMigrationMetadata();
    if (!migrationMetadata.isMockDataCreated()) {

      LOGGER.info("Persisting mock data...");

      migrationMetadata.setMockDataCreated(true);
      database.persist(migrationMetadata);

      MockDataProvider.persistTo(onboardingRepository);
    }
  }

}
