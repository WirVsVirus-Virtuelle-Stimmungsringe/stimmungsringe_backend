package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.InMemoryDatastore;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Message;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.dynamodb.DataMapper;
import de.wirvsvirus.hack.repository.dynamodb.GroupData;
import de.wirvsvirus.hack.repository.dynamodb.MessageData;
import de.wirvsvirus.hack.repository.dynamodb.UserData;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("dynamodb")
public class OnboardingRepositoryDynamoDB implements OnboardingRepository {

    private OnboardingRepositoryInMemory memory;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    // control flushing of dirty data from memory
    private AtomicInteger memoryVersion;
    private AtomicInteger databaseVersion;

    @PostConstruct
    public void startup() {

        memory = new OnboardingRepositoryInMemory();
//        memory.initMock();
        InMemoryDatastore.allUsers.clear(); // FIXME
        log.info("Do not load mock data");

        prepareTable(UserData.class, false);
        prepareTable(GroupData.class, false);

        restoreFromStorage();

        memoryVersion = new AtomicInteger(1);
        databaseVersion = new AtomicInteger(1);
        // flush to make sure that patched data get persisted
        markForFlush();

    }

    @Scheduled(initialDelayString = "PT10S", fixedDelayString = "PT0.500S")
    public void autoFlushStorage() {
        final int currentMemoryVersion = memoryVersion.get();
        final int currentDatabaseVersion = databaseVersion.get();
        if (currentMemoryVersion == currentDatabaseVersion) {
            log.trace("Memory and database version are in sync: {}", currentMemoryVersion);
            return;
        }
        Preconditions.checkState(currentMemoryVersion > currentDatabaseVersion);

        log.debug("Bump database version to {} - not flushed yet", currentMemoryVersion);
        final boolean updated = databaseVersion.compareAndSet(currentDatabaseVersion, currentMemoryVersion);
        // note: CAS is useless ... actually databaseVersion is not subject to concurrent access; it's exclusively controlled byp this method
        Preconditions.checkState(updated, "CAS update failed - should never happen!");

        try {
            writeDataToStorage();
            log.debug("Flushed data version {}", currentMemoryVersion);
        } catch (final Exception ex) {
            log.error("Failed to flush data version {}", currentDatabaseVersion);
        }
    }

    private  <T> void prepareTable(final Class<T> clazz, final boolean tryDeleteBefore) {
        if (tryDeleteBefore) {
            try {
                DeleteTableRequest deleteTableRequest = dynamoDBMapper
                        .generateDeleteTableRequest(clazz);
                amazonDynamoDB.deleteTable(deleteTableRequest);
                log.warn("Table {} deleted", clazz.getSimpleName());
            } catch(ResourceNotFoundException rnfe) {
                log.warn("Table {} des not exist", clazz.getSimpleName());
            }
        }

        try {
            CreateTableRequest tableRequest = dynamoDBMapper
                    .generateCreateTableRequest(clazz);

            tableRequest.setProvisionedThroughput(
                    new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
            log.warn("Table {} created", clazz.getSimpleName());
        } catch (ResourceInUseException riue) {
            log.warn("Table {} already exists", clazz.getSimpleName());
            // nothing
        }

    }

    private void markForFlush() {
        Preconditions.checkNotNull(memoryVersion);
        memoryVersion.incrementAndGet();
    }

    /**
     * note: use only from autoflush facility
     */
    private synchronized void writeDataToStorage() {
        log.debug("Flushing to storage ....");
        final StopWatch stopWatch = StopWatch.createStarted();

        int countUsers = 0;
        int countGroups = 0;
        int countMessages = 0;

        {
            for (final User user : InMemoryDatastore.allUsers.values()) {
                // TODO tune: reduce consistency
                dynamoDBMapper.save(DataMapper.dataFromUser(user,
                        findSentimentByUserId(user.getUserId()),
                        findLastStatusUpdateByUserId(user.getUserId())
                ));
                countUsers++;
            }

        }

        {
            for (final Group group : InMemoryDatastore.allGroups.values()) {
                // TODO tune: reduce consistency
                dynamoDBMapper.save(DataMapper.dataFromGroup(group, membersByGroup(group.getGroupId())));
                countGroups++;
            }

        }

        {
            for (final List<Message> messages : InMemoryDatastore.allGroupMessages.values()) {
                for (final Message message : messages) {
                    dynamoDBMapper.save(DataMapper.dataFromMessage(message));
                    countMessages++;
                }
            }
        }

        log.debug("Flushed {} users and {} groups to database in {}ms",
                countUsers, countGroups, stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private List<UUID> membersByGroup(UUID groupId) {
        return EntryStream.of(InMemoryDatastore.groupByUserId)
                .filterValues(gid -> gid.equals(groupId))
                .keys()
                .collect(Collectors.toList());
    }

    private synchronized void restoreFromStorage() {
        int countUsers = 0;
        int countGroups = 0;
        int countMessages = 0;

        {
            // TODO tune
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<UserData> result = dynamoDBMapper.scan(UserData.class, scanAll);
            for (UserData userData : result) {
                System.out.println("- " + userData);
                Preconditions.checkState(!InMemoryDatastore.allUsers.containsKey(userData.getUserId()));

                InMemoryDatastore.allUsers.put(userData.getUserId(), DataMapper.userFromDatabase(userData));
                InMemoryDatastore.sentimentByUser.put(userData.getUserId(), Sentiment.valueOf(userData.getSentiment()));
                InMemoryDatastore.lastStatusUpdateByUser.put(userData.getUserId(), DataMapper.lastStatusUpdateFromDatabase(userData));
                countUsers++;
            }

        }

        {
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<GroupData> result = dynamoDBMapper.scan(GroupData.class, scanAll);
            for (GroupData groupData : result) {
                System.out.println("- " + groupData);
                Preconditions.checkState(!InMemoryDatastore.allGroups.containsKey(groupData.getGroupId()));

                final Pair<Group, List<UUID>> pair = DataMapper.groupFromDatabase(groupData);
                InMemoryDatastore.allGroups.put(groupData.getGroupId(), pair.getLeft());
                pair.getRight().forEach(memberId -> InMemoryDatastore.groupByUserId.put(memberId, groupData.getGroupId()));
                InMemoryDatastore.allGroupMessages.put(groupData.getGroupId(), new ArrayList<>());

                countGroups++;
            }
        }

        {
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<MessageData> result = dynamoDBMapper.scan(MessageData.class, scanAll);
            for (MessageData messageData : result) {
                final Message message = DataMapper.messageFromDatabase(messageData);
                InMemoryDatastore.allGroupMessages.putIfAbsent(message.getGroupId(), new ArrayList<>());
                InMemoryDatastore.allGroupMessages.get(message.getGroupId()).add(message);

                countMessages++;
            }
        }

        log.debug("Restored {} users and {} groups to database", countUsers, countGroups);
    }

    @Override
    public Optional<Group> findGroupById(final UUID groupId) {
        return memory.findGroupById(groupId);
    }

    @Override
    public void createNewUser(final User newUser, final Sentiment sentiment, final Instant lastUpdate) {
        memory.createNewUser(newUser, sentiment, lastUpdate);
        markForFlush();
    }

    @Override
    public User lookupUserById(final UUID userId) {
        return memory.lookupUserById(userId);
    }

    @Override
    public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
        memory.updateUser(userId, userSettings);
        markForFlush();
    }

    @Override
    public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
        memory.updateGroup(groupId, groupSettings);
        markForFlush();
    }

    @Override
    public Group startNewGroup(final String groupName, final String groupCode) {
        final Group group = memory.startNewGroup(groupName, groupCode);
        markForFlush();
        return group;
    }


    @Override
    public void joinGroup(final UUID groupId, final UUID userId) {
        memory.joinGroup(groupId, userId);
        markForFlush();
    }

    @Override
    public void leaveGroup(final UUID groupId, final UUID userId) {
        memory.leaveGroup(groupId, userId);
        markForFlush();
    }

    @Override
    public List<User> findOtherUsersInGroup(UUID groupId, UUID currentUserId) {
        return memory.findOtherUsersInGroup(groupId, currentUserId);
    }

    @Override
    public Sentiment findSentimentByUserId(final UUID userId) {
        return memory.findSentimentByUserId(userId);
    }

    @Override
    public Instant findLastStatusUpdateByUserId(final UUID userId) {
        return memory.findLastStatusUpdateByUserId(userId);
    }

    @Override
    public void touchLastStatusUpdate(final UUID userId) {
        memory.touchLastStatusUpdate(userId);
        markForFlush();
    }

    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        memory.updateStatus(userId, sentiment);
        markForFlush();
    }

    @Override
    public Optional<Group> findGroupByUser(final UUID userId) {
        return memory.findGroupByUser(userId);
    }

    @Override
    public Optional<Group> findGroupByCode(final String groupCode) {
        return memory.findGroupByCode(groupCode);
    }

    @Override
    public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return memory.findByDeviceIdentifier(deviceIdentifier);
    }

    @Override
    public void sendMessage(final User sender, final User recipient, final String text) {
        memory.sendMessage(sender, recipient, text);
        markForFlush();
    }

    @Override
    public List<Message> findMessagesByRecipientId(final UUID userId) {
        return memory.findMessagesByRecipientId(userId);
    }

    @Override
    public void clearMessagesByRecipientId(final UUID userId) {
        memory.clearMessagesByRecipientId(userId);
        markForFlush();
    }
}
