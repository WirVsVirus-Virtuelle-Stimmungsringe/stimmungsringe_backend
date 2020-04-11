package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.Lists;
import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.repository.dynamodb.GroupData;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.dynamodb.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

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

    @Override
    public User findByUserId(final UUID userId) {
        return memory.findByUserId(userId);
    }


    @Override
    public void startNewGroup(final String groupName) {
        memory.startNewGroup(groupName);
    }


    @Override
    public void joinGroup(final String groupName, final UUID userId) {
        memory.joinGroup(groupName, userId);
    }


    @Override
    public List<User> findOtherUsersInGroup(final UUID userId) {
        return memory.findOtherUsersInGroup(userId);
    }

    @Override
    public Sentiment findSentimentByUserId(final UUID userId) {
        return findSentimentByUserId(userId);
    }

    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {
        memory.updateStatus(userId, sentiment);
    }

    @Override
    public Optional<String> findGroupNameByUser(final UUID userId) {
        return memory.findGroupNameByUser(userId);
    }

    @Override
    public Optional<String> findGroupByName(final String groupName) {
        return memory.findGroupByName(groupName);
    }

    @Override
    public Optional<User> findByDeviceIdentifier(final String deviceIdentifier) {
        return memory.findByDeviceIdentifier(deviceIdentifier);
    }

    @Override
    public Optional<String> findGroupNameForUser(final UUID userId) {
        return memory.findGroupNameByUser(userId);
    }
}
