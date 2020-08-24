package de.wirvsvirus.hack.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import de.wirvsvirus.hack.model.Group;
import de.wirvsvirus.hack.model.Sentiment;
import de.wirvsvirus.hack.model.User;
import de.wirvsvirus.hack.repository.OnboardingRepositoryDynamoDB;
import de.wirvsvirus.hack.repository.dynamodb.MessageData;
import de.wirvsvirus.hack.repository.dynamodb.UserData;
import de.wirvsvirus.hack.spring.DynamoDBConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.UUID;

/**
 * https://docs.aws.amazon.com/de_de/amazondynamodb/latest/developerguide/CodeSamples.Java.html
 * https://docs.aws.amazon.com/de_de/amazondynamodb/latest/developerguide/DynamoDBMapper.CRUDExample1.html
 *
 */
@ExtendWith (SpringExtension.class)
@SpringBootTest(classes = {DynamoDBConfiguration.class, OnboardingRepositoryDynamoIT.TestConfiguration.class})
@TestPropertySource(properties = {
        "dynamodb.table_prefix=itest",
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=fakeMyKeyId",
        "amazon.aws.secretkey=fakeSecretAccessKey" })
@ActiveProfiles("dynamodb")
@Disabled
public class OnboardingRepositoryDynamoIT {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private UUID userId1 = UUID.randomUUID();
    private UUID userId2 = UUID.randomUUID();

    @Autowired
    private OnboardingRepositoryDynamoDB onboardingRepositoryDynamoDB;

    @BeforeEach
    public void setup() throws Exception {
    }

    @Test
    public void createNewGroupAndSendMessage() {
        final User user1 = new User(userId1, "dev01");
        onboardingRepositoryDynamoDB.createNewUser(user1, Sentiment.cloudyNight, Instant.now());

        final User user2 = new User(userId2, "dev02");
        onboardingRepositoryDynamoDB.createNewUser(user2, Sentiment.sunnyWithClouds, Instant.now());

        onboardingRepositoryDynamoDB.autoFlushStorage();

        final Group group = onboardingRepositoryDynamoDB.startNewGroup("die zwei", "919191");
        onboardingRepositoryDynamoDB.joinGroup(group.getGroupId(), user1.getUserId());
        onboardingRepositoryDynamoDB.joinGroup(group.getGroupId(), user2.getUserId());

        onboardingRepositoryDynamoDB.sendMessage(user1, user2, "Du bist so toll!");

        onboardingRepositoryDynamoDB.autoFlushStorage();

        logTableRows(MessageData.class);

    }

    private <T> void logTableRows(final Class<T> dataClazz) {
        final DynamoDBScanExpression scan = new DynamoDBScanExpression();
        final PaginatedScanList<T> result = dynamoDBMapper.scan(dataClazz, scan);
        result.forEach(p -> System.out.println("- " + p.toString()));
    }

    @Configuration
    @Import({
            OnboardingRepositoryDynamoDB.class
    })
    static class TestConfiguration {

    }
}
