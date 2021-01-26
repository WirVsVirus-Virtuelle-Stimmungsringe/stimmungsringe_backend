package de.wirvsvirus.hack.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.*;
import de.wirvsvirus.hack.spring.DynamoDBConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * https://docs.aws.amazon.com/de_de/amazondynamodb/latest/developerguide/CodeSamples.Java.html
 * https://docs.aws.amazon.com/de_de/amazondynamodb/latest/developerguide/DynamoDBMapper.CRUDExample1.html
 *
 */
@ExtendWith (SpringExtension.class)
@SpringBootTest(classes = {DynamoDBConfiguration.class})
@TestPropertySource(properties = {
        "dynamodb.table_prefix=itest",
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=fakeMyKeyId",
        "amazon.aws.secretkey=fakeSecretAccessKey" })
@ActiveProfiles("dynamodb")
@Disabled
public class DynamoDBIntegrationTest {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @BeforeEach
    public void setup() throws Exception {

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
//            dynamoDBMapper.save(dave);
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
    void getItem() {

        final ProductInfo p1224 = new ProductInfo();
        p1224.setId("1224");
        p1224.setCost("32");
        p1224.setMsrp("222");
        dynamoDBMapper.save(p1224);
        final ProductInfo productInfo = dynamoDBMapper.load(ProductInfo.class, "324d4db2-edf1-4292-b1ef-27c59db66f25");
        System.out.println("> " + productInfo);
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
        result.forEach(p -> System.out.println("- " + p.getId()));
    }
}
