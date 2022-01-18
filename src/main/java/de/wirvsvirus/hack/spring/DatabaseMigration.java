package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.mock.MockDataProvider;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserStatus;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import one.microstream.storage.types.StorageManager;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// run after repositories
@Service
@Slf4j
public class DatabaseMigration {

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private Database database;

  @Autowired
  private StorageManager storageManager;

  @PostConstruct
  public void runMigrations() {
    final MigrationMetadata migrationMetadata = database.dataRoot().getMigrationMetadata();
    if (!migrationMetadata.isMockDataCreated()) {

      log.info("Persisting mock data...");

      migrationMetadata.setMockDataCreated(true);
      database.persist(migrationMetadata);

      MockDataProvider.persistTo(onboardingRepository);
    }

    log.info("Reading migration metadata: {}", migrationMetadata);
    final int prevMigrationHash = migrationMetadata.hashCode();

    if (!migrationMetadata.isUserStatusMapInitialized()) {
      migrationMetadata.setUserStatusMapInitialized(true);
      database.persist(migrationMetadata);

      if (database.dataRoot().getStatusByUser() == null) {
        database.dataRoot().setStatusByUser(new HashMap<>());
        storageManager.storeRoot();
      }
    }

    if (!migrationMetadata.isUserStatusMapPopulatedFromExistingUsers()) {
      migrationMetadata.setUserStatusMapPopulatedFromExistingUsers(true);
      database.persist(migrationMetadata);

      final Map<UUID, UserStatus> statusByUser = database.dataRoot().getStatusByUser();
      for (final UUID userId : database.dataRoot().getAllUsers().keySet()) {
        if (!statusByUser.containsKey(userId)) {
          final UserStatus initial = new UserStatus();
          initial.setSentiment(Sentiment.cloudy);
          initial.setLastStatusUpdate(Instant.now());
          statusByUser.put(userId, initial);
          log.info("- init user status for {}", userId);
        }
      }
      database.persist(statusByUser);
    }

    if (!migrationMetadata.isLastSigninInitialized()) {
      migrationMetadata.setLastSigninInitialized(true);
      database.persist(migrationMetadata);

      EntryStream.of(database.dataRoot().getStatusByUser())
          .values()
          .filter(userStatus -> userStatus.getLastSignin() == null)
          .forEach(userStatus -> {
            userStatus.setLastSignin(
                userStatus.getLastStatusUpdate() != null
                    ? userStatus.getLastStatusUpdate()
                    : Instant.parse("2019-01-01T10:15:30.00Z"));
            database.persist(userStatus);
          });
    }

    if (!migrationMetadata.isSentimentTextInitialized2()) {
      migrationMetadata.setSentimentTextInitialized2(true);
      database.persist(migrationMetadata);

      EntryStream.of(database.dataRoot().getStatusByUser())
          .values()
          .filter(userStatus -> userStatus.getSentimentText() == null)
          .forEach(userStatus ->  {
            userStatus.setSentimentText("");
            database.persist(userStatus);
          });
    }

    // delete messages for non-existing users
    if (!migrationMetadata.isStaleMessagesDeleted()) {
      migrationMetadata.setStaleMessagesDeleted(true);
      database.persist(migrationMetadata);

      final Set<UUID> staleMessages =
          EntryStream.of(database.dataRoot().getAllGroupMessages())
              .flatMapValues(Collection::stream)
              .values()
              .filter(message -> !onboardingRepository.isUserExisting(message.getSenderUserId()))
              .map(Message::getMessageId)
              .toImmutableSet();

      EntryStream.of(database.dataRoot().getAllGroupMessages())
          .values()
          .forEach(messageList -> {
            messageList.removeIf(m -> staleMessages.contains(m.getMessageId()));
            database.persist(messageList);
          });
    }

    if (!migrationMetadata.isLastSigninInitialized4()) {
      migrationMetadata.setLastSigninInitialized4(true);
      database.persist(migrationMetadata);

      EntryStream.of(database.dataRoot().getStatusByUser())
          .values()
          .filter(userStatus -> userStatus.getLastSignin() == null)
          .forEach(userStatus -> {
            userStatus.setLastSignin(
                userStatus.getLastSignin() != null
                    ? userStatus.getLastSignin()
                    : Instant.parse("2019-01-01T10:15:32.00Z"));
            database.persist(userStatus);
          });
    }

    if (migrationMetadata.hashCode() == prevMigrationHash) {
      log.info("Keep migration metadata unchanged: {}", migrationMetadata);
    } else {
      log.info("Updated migration metadata: {}", migrationMetadata);
    }

  }

}
