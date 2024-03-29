package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.mock.MockDataProvider;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory;
import de.wirvsvirus.hack.model.UserGroupMembershipHistory.Change;
import de.wirvsvirus.hack.model.UserStatus;
import de.wirvsvirus.hack.repository.OnboardingRepository;
import de.wirvsvirus.hack.repository.microstream.MigrationMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// run after repositories
@Service
@Slf4j
public class DatabaseMigration {

  @Value("${database.migration.auto-run}")
  private boolean autoRun;

  @Autowired
  private OnboardingRepository onboardingRepository;

  @Autowired
  private Database database;

  @Autowired
  private StorageManager storageManager;

  @PostConstruct
  public void autoRunOnStartup() {
    log.info("Migration auto-run={}", autoRun);

    if (autoRun) {
      runMigrations();
    }
  }

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

    if (!migrationMetadata.isGroupCreatedAtInitialized()) {
      migrationMetadata.setGroupCreatedAtInitialized(true);
      database.persist(migrationMetadata);

      EntryStream.of(database.dataRoot().getAllGroups())
          .values()
          .filter(group -> group.getCreatedAt() == null)
          .forEach(group -> {
            group.setCreatedAt(Instant.parse("2022-01-01T11:11:11.00Z"));
            database.persist(group);
          });
    }

    if (!migrationMetadata.isHistoryUserStatusChangesInitialized()) {
      migrationMetadata.setHistoryUserStatusChangesInitialized(true);
      database.persist(migrationMetadata);

      if (database.dataRoot().getHistoryUserStatusChanges() == null) {
        database.dataRoot().setHistoryUserStatusChanges(new ArrayList<>());
        storageManager.storeRoot();
      }
    }

    if (!migrationMetadata.isHistoryUserGroupMembershipInitialized()) {
      migrationMetadata.setHistoryUserGroupMembershipInitialized(true);
      database.persist(migrationMetadata);

      if (database.dataRoot().getHistoryUserGroupMembership() == null) {
        database.dataRoot().setHistoryUserGroupMembership(new ArrayList<>());
        storageManager.storeRoot();
      }
    }

    // if no historic user-group membership is tracked, initialize with one JOIN event
    if (!migrationMetadata.isHistoryOfGroupMembershipInitialized()) {
      migrationMetadata.setHistoryOfGroupMembershipInitialized(true);
      database.persist(migrationMetadata);

      final List<UserGroupMembershipHistory> groupMembership = database.dataRoot()
          .getHistoryUserGroupMembership();
      final Set<UUID> allUserIds = database.dataRoot().getAllUsers().keySet();
      final Set<UUID> allGroupIds = database.dataRoot().getAllGroups().keySet();

      EntryStream.of(database.dataRoot().getGroupByUserId())
          .filterKeys(allUserIds::contains)
          .filterValues(allGroupIds::contains)
          .forKeyValue((userId, groupId) -> {
            final UserGroupMembershipHistory membership = new UserGroupMembershipHistory();
            membership.setChange(Change.JOIN);
            membership.setUserId(userId);
            membership.setGroupId(groupId);
            membership.setTimestamp(Instant.parse("2020-01-01T11:11:11.00Z"));
            groupMembership.add(membership);
          });
      database.persist(groupMembership);
    }

    if (!migrationMetadata.isAchievementShownStatusByUserInitialized()) {
      migrationMetadata.setAchievementShownStatusByUserInitialized(true);
      database.persist(migrationMetadata);

      if (database.dataRoot().getAchievementShownStatusByUserAndType() == null) {
        database.dataRoot().setAchievementShownStatusByUserAndType(new HashMap<>());
        storageManager.storeRoot();
      }
    }


    // ^^^ insert migrations above this fold

    if (migrationMetadata.hashCode() == prevMigrationHash) {
      log.info("Keep migration metadata unchanged: {}", migrationMetadata);
    } else {
      log.info("Updated migration metadata: {}", migrationMetadata);
    }

  }

}
