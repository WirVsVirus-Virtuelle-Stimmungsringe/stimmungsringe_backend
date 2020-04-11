package de.wirvsvirus.hack.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.*;
import de.wirvsvirus.hack.Application;
import de.wirvsvirus.hack.model.ProductInfo;
import de.wirvsvirus.hack.spring.DynamoDBConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith (SpringExtension.class)
@SpringBootTest(classes = DynamoDBConfiguration.class)
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=fakeMyKeyId",
        "amazon.aws.secretkey=fakeSecretAccessKey" })
public class DynamoDBIntegrationTest {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @BeforeEach
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        DeleteTableRequest deleteTableRequest = dynamoDBMapper
                .generateDeleteTableRequest(ProductInfo.class);

        try {
            amazonDynamoDB.deleteTable(deleteTableRequest);
        } catch(ResourceNotFoundException rnfe) {
        }

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(ProductInfo.class);

        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        amazonDynamoDB.createTable(tableRequest);

        for (int i=10; i<99;i++) {
            ProductInfo dave = new ProductInfo();
            dave.setCost("25");
            dave.setMsrp("zzzz " + i);
            dynamoDBMapper.save(dave);
        }

        //...

//        dynamoDBMapper.batchDelete(
//                (List<ProductInfo>)repository.findAll());
    }

    @Test
    public void sampleTestCase() {
        ProductInfo dave = new ProductInfo();
        dave.setCost("25");
        dave.setMsrp("zzzz");
        dynamoDBMapper.save(dave);
    }

    @Test
    void findall() {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS("e53abae7-69e7-42b4-bdee-6d392b2ed80d"));

        final DynamoDBQueryExpression<ProductInfo> scxxan = new DynamoDBQueryExpression<ProductInfo>()
                .withKeyConditionExpression("id = :val1").withExpressionAttributeValues(eav);

        final DynamoDBScanExpression scan = new DynamoDBScanExpression()
                ;

        final PaginatedScanList<ProductInfo> result = dynamoDBMapper.scan(ProductInfo.class, scan);
        result.forEach(p -> System.out.println("- " + p.getMsrp()));
    }
}
