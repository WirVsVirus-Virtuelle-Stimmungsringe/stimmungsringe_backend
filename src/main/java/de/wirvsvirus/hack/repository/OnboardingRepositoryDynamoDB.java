package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
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

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;


    @PostConstruct
    public void startup() {

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
        return null;
    }

    @Override
    public void startNewGroup(final String groupName) {

    }

    @Override
    public void joinGroup(final String groupName, final UUID userId) {

    }

    @Override
    public List<User> findOtherUsersInGroup(final UUID userId) {
        return null;
    }

    @Override
    public Sentiment findSentimentByUserId(final UUID userId) {
        return null;
    }

    @Override
    public void updateStatus(final UUID userId, final Sentiment sentiment) {

    }

    @Override
    public Optional<String> findGroupNameByUser(final UUID userId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findGroupByName(final String groupName) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findGroupNameForUser(final UUID id) {
        return Optional.empty();
    }
}
