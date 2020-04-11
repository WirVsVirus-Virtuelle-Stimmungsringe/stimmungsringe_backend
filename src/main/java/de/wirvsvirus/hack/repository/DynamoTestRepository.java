package de.wirvsvirus.hack.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import de.wirvsvirus.hack.model.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class DynamoTestRepository {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    ProductInfoRepository repository;


    @PostConstruct
    public void startup() {

        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(ProductInfo.class);
        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        if(!amazonDynamoDB.listTables().getTableNames().contains(tableRequest.getTableName())) {
            amazonDynamoDB.createTable(tableRequest);
        }

            ProductInfo dave = new ProductInfo();
            dave.setCost("20");
            dave.setMsrp("zzzz");
            repository.save(dave);

            List<ProductInfo> result
                    = (List<ProductInfo>) repository.findAll();

            result.forEach(row -> System.out.println("row " + row.getCost()));

    }

}
