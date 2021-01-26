package de.wirvsvirus.hack.spring;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dynamodb")
public class DynamoDBConfiguration {

    @Value("${amazon.dynamodb.endpoint:}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Value("${dynamodb.table_prefix}")
    private String tablePrefix;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        final AmazonDynamoDB amazonDynamoDB;

        if (StringUtils.isNotBlank(amazonDynamoDBEndpoint)) {
            amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials()))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                            amazonDynamoDBEndpoint, Regions.EU_WEST_1.getName()))
                    .build();
        } else {
             amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials()))
                    .withRegion(Regions.EU_WEST_1)
                    .build();
        }

        return amazonDynamoDB;
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        final String databasePrefix = DynamoDBTablePrefix.valueOf(tablePrefix.toUpperCase()).name().toLowerCase();

        final DynamoDBMapperConfig.TableNameOverride override = DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(
                databasePrefix + "_");

        final DynamoDBMapperConfig mapperconfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(override).build();
        return new DynamoDBMapper(amazonDynamoDB, mapperconfig);
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(
                amazonAWSAccessKey, amazonAWSSecretKey);
    }

    enum DynamoDBTablePrefix {
        /**
         * localhost:8000
         */
        LOCALDEV,
        /**
         * AWS
         */
        INTEG,
        /**
         * local integration test
         */
        ITEST
    }

}
