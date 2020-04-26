package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.dynamodb.GroupData;
import de.wirvsvirus.hack.repository.dynamodb.DataMapper;
import de.wirvsvirus.hack.repository.dynamodb.UserData;
import de.wirvsvirus.hack.service.dto.GroupSettingsDto;
import de.wirvsvirus.hack.service.dto.UserSettingsDto;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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


    @PostConstruct
    public void startup() {

        memory = new OnboardingRepositoryInMemory();
//        memory.initMock();
        MockFactory.allUsers.clear(); // FIXME
        log.info("Do not load mock data");



        prepareTable(UserData.class, false);
        prepareTable(GroupData.class, false);

        restoreFromStorage();
        // flush to make sure that fixed get persisted
        flushToStorage();

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

    private synchronized void flushToStorage() {

        int countUsers = 0;
        int countGroups = 0;

        {
            for (final User user : MockFactory.allUsers.values()) {
                // TODO tune: reduce consistency, store async
                dynamoDBMapper.save(DataMapper.dataFromUser(user, findSentimentByUserId(user.getUserId())));
                countUsers++;
            }

        }

        {
            for (final Group group : MockFactory.allGroups.values()) {
                // TODO tune: reduce consistency, store async
                dynamoDBMapper.save(DataMapper.dataFromGroup(group, membersByGroup(group.getGroupId())));
                countGroups++;
            }

        }

        log.debug("Flushed {} users and {} groups to database", countUsers, countGroups);
    }

    private List<UUID> membersByGroup(UUID groupId) {
        return EntryStream.of(MockFactory.groupByUserId)
            .filterValues(gid -> gid.equals(groupId))
            .keys()
            .collect(Collectors.toList());
    }

    private synchronized void restoreFromStorage() {
        int countUsers = 0;
        int countGroups = 0;

        {
            // TODO tune
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<UserData> result = dynamoDBMapper.scan(UserData.class, scanAll);
            for (UserData userData : result) {
                System.out.println("- " + userData);
                Preconditions.checkState(!MockFactory.allUsers.containsKey(userData.getUserId()));

                MockFactory.allUsers.put(userData.getUserId(), DataMapper.userFromDatabase(userData));
                MockFactory.sentimentByUser.put(userData.getUserId(), Sentiment.valueOf(userData.getSentiment()));
                countUsers++;
            }

        }

        {
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<GroupData> result = dynamoDBMapper.scan(GroupData.class, scanAll);
            for (GroupData groupData : result) {
                System.out.println("- " + groupData);
                Preconditions.checkState(!MockFactory.allGroups.containsKey(groupData.getGroupId()));

                final Pair<Group, List<UUID>> pair = DataMapper.groupFromDatabase(groupData);
                MockFactory.allGroups.put(groupData.getGroupId(), pair.getLeft());
                pair.getRight().forEach(memberId -> MockFactory.groupByUserId.put(memberId, groupData.getGroupId()));
                countGroups++;
            }
        }

        log.debug("Restored {} users and {} groups to database", countUsers, countGroups);
    }

    @Override
    public Optional<Group> findGroupById(final UUID groupId) {
        return memory.findGroupById(groupId);
    }

    @Override
    public void createNewUser(final User newUser, final Sentiment sentiment) {
        memory.createNewUser(newUser, sentiment);
        flushToStorage();
    }

    @Override
    public User lookupUserById(final UUID userId) {
        return memory.lookupUserById(userId);
    }

    @Override
    public void updateUser(final UUID userId, final UserSettingsDto userSettings) {
        memory.updateUser(userId, userSettings);
    }

    @Override
    public void updateGroup(final UUID groupId, final GroupSettingsDto groupSettings) {
        memory.updateGroup(groupId, groupSettings);
    }

    @Override
    public Group startNewGroup(final String groupName, final String groupCode) {
        final Group group = memory.startNewGroup(groupName, groupCode);
        flushToStorage();
        return group;
    }


    @Override
    public void joinGroup(final UUID groupId, final UUID userId) {
        memory.joinGroup(groupId, userId);
        flushToStorage();
    }

    @Override
    public void leaveGroup(final UUID groupId, final UUID userId) {
        memory.leaveGroup(groupId, userId);
        flushToStorage();
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
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        memory.updateStatus(userId, sentiment);
        flushToStorage();
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
        flushToStorage();
        return memory.findByDeviceIdentifier(deviceIdentifier);
    }

    @Override
    public Optional<Group> findGroupForUser(final UUID userId) {
        return memory.findGroupByUser(userId);
    }
}
