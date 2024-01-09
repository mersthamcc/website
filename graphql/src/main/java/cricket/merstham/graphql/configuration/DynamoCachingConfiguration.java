package cricket.merstham.graphql.configuration;

import cricket.merstham.graphql.cache.DynamoCacheConfiguration;
import cricket.merstham.graphql.cache.DynamoCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableClass;
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification;
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest;

import static java.util.Objects.nonNull;

@Configuration
@Profile("dynamo-caching")
public class DynamoCachingConfiguration {
    @Bean
    public CacheManager cacheManager(
            DynamoDbClient client, DynamoCacheConfiguration configuration) {
        if (configuration.isCreateTable() && !tableExists(client, configuration)) {
            createTable(client, configuration);
        }

        return new DynamoCacheManager(client, configuration);
    }

    @Bean
    public DynamoDbClient dynamoDbClient(DynamoCacheConfiguration configuration) {
        var builder =
                DynamoDbClient.builder()
                        .credentialsProvider(DefaultCredentialsProvider.builder().build())
                        .region(Region.of(configuration.getRegion()));
        if (nonNull(configuration.getEndpoint())) {
            builder.endpointOverride(configuration.getEndpoint());
        }
        return builder.build();
    }

    private static void createTable(DynamoDbClient client, DynamoCacheConfiguration configuration) {
        client.createTable(
                CreateTableRequest.builder()
                        .tableName(configuration.getTableName())
                        .billingMode("PAY_PER_REQUEST")
                        .tableClass(TableClass.STANDARD)
                        .attributeDefinitions(
                                AttributeDefinition.builder()
                                        .attributeName(configuration.getCacheNameAttributeName())
                                        .attributeType(ScalarAttributeType.S)
                                        .build(),
                                AttributeDefinition.builder()
                                        .attributeName(configuration.getKeyAttributeName())
                                        .attributeType(ScalarAttributeType.B)
                                        .build())
                        .keySchema(
                                KeySchemaElement.builder()
                                        .attributeName(configuration.getCacheNameAttributeName())
                                        .keyType(KeyType.HASH)
                                        .build(),
                                KeySchemaElement.builder()
                                        .attributeName(configuration.getKeyAttributeName())
                                        .keyType(KeyType.RANGE)
                                        .build())
                        .build());
        client.updateTimeToLive(
                UpdateTimeToLiveRequest.builder()
                        .tableName(configuration.getTableName())
                        .timeToLiveSpecification(
                                TimeToLiveSpecification.builder()
                                        .attributeName(configuration.getTimeToLiveAttributeName())
                                        .enabled(true)
                                        .build())
                        .build());
    }

    private static boolean tableExists(
            DynamoDbClient client, DynamoCacheConfiguration configuration) {
        try {
            client.describeTable(
                    DescribeTableRequest.builder().tableName(configuration.getTableName()).build());
            return true;
        } catch (ResourceNotFoundException ignored) {
            return false;
        }
    }
}
