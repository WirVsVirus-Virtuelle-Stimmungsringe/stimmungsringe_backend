package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.google.common.base.Preconditions;
import de.wirvsvirus.hack.mock.MockFactory;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.dynamodb.GroupData;
import de.wirvsvirus.hack.repository.dynamodb.Mapper;
import de.wirvsvirus.hack.repository.dynamodb.UserData;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
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

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;


    @PostConstruct
    public void startup() {

        memory = new OnboardingRepositoryInMemory();
//        memory.initMock();

        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        prepareTable(UserData.class);
        prepareTable(GroupData.class);

    }

    private <T> void prepareTable(final Class<T> clazz) {
        try {
            DeleteTableRequest deleteTableRequest = dynamoDBMapper
                .generateDeleteTableRequest(clazz);
            amazonDynamoDB.deleteTable(deleteTableRequest);
        } catch(ResourceNotFoundException rnfe) {
        }
        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(clazz);

        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        amazonDynamoDB.createTable(tableRequest);
    }

    public void flushToStorage() {

        int countUsers = 0;
        int countGroups = 0;

        {
            for (final User user : MockFactory.allUsers.values()) {
                // TODO tune: reduce consistency, store async
                dynamoDBMapper.save(Mapper.dataFromUser(user, MockFactory.sentimentByUser(user.getUserId())));
                countUsers++;
            }

            log.debug("Flushed {} users to database", countUsers);
        }

        {
            for (final Group group : MockFactory.allGroups.values()) {
                // TODO tune: reduce consistency, store async
                dynamoDBMapper.save(Mapper.dataFromGroup(group, membersByGroup(group.getGroupId())));
                countGroups++;
            }

            log.debug("Flushed {} users and {} groups to database", countUsers, countGroups);
        }

    }

    private List<UUID> membersByGroup(UUID groupId) {
        return EntryStream.of(MockFactory.groupByUserId)
            .filterValues(gid -> gid.equals(groupId))
            .keys()
            .collect(Collectors.toList());
    }

    public void restoreFromStorage() {
        {
            // TODO tune
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<UserData> result = dynamoDBMapper.scan(UserData.class, scanAll);
            for (UserData userData : result) {
                Preconditions.checkState(!MockFactory.allUsers.containsKey(userData.getUserId()));

                MockFactory.allUsers.put(userData.getUserId(), Mapper.userFromDatabase(userData));
                MockFactory.sentimentByUser.put(userData.getUserId(), Sentiment.valueOf(userData.getSentiment()));
            }

        }

        {
            final DynamoDBScanExpression scanAll = new DynamoDBScanExpression();

            final PaginatedScanList<GroupData> result = dynamoDBMapper.scan(GroupData.class, scanAll);
            for (GroupData groupData : result) {
                Preconditions.checkState(!MockFactory.allGroups.containsKey(groupData.getGroupId()));

                MockFactory.allGroups.put(groupData.getGroupId(), Mapper.groupFromDatabase(groupData));

            }
        }

    }

    @Override
    public Optional<Group> findGroupById(final UUID groupId) {
        return memory.findGroupById(groupId);
    }

    @Override
    public void createNewUser(final User newUser) {
        memory.createNewUser(newUser);
    }

    @Override
    public User lookupUserById(final UUID userId) {
        return memory.lookupUserById(userId);
    }

    @Override
    public Group startNewGroup(final String groupName) {
        return memory.startNewGroup(groupName);
    }


    @Override
    public void joinGroup(final UUID groupId, final UUID userId) {
        memory.joinGroup(groupId, userId);
    }


    @Override
    public List<User> findOtherUsersInGroup(final UUID userId) {
        return memory.findOtherUsersInGroup(userId);
    }

    @Override
    public Sentiment findSentimentByUserId(final UUID userId) {
        return memory.findSentimentByUserId(userId);
    }

    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        memory.updateStatus(userId, sentiment);
    }

    @Override
    public Optional<Group> findGroupByUser(final UUID userId) {
        return memory.findGroupByUser(userId);
    }

    @Override
    public Optional<Group> findGroupByName(final String groupName) {
        return memory.findGroupByName(groupName);
    }

    @Override
    public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return memory.findByDeviceIdentifier(deviceIdentifier);
    }

    @Override
    public Optional<Group> findGroupNameForUser(final UUID userId) {
        return memory.findGroupByUser(userId);
    }
}
