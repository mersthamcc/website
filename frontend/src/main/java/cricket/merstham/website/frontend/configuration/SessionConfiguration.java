package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.session.DynamoSessionConfiguration;
import cricket.merstham.website.frontend.session.DynamoSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
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
@Profile("dynamo-sessions")
public class SessionConfiguration extends SpringHttpSessionConfiguration {
    @Bean
    public DynamoSessionRepository sessionRepository(
            DynamoDbClient client, DynamoSessionConfiguration configuration) {
        if (configuration.isCreateTable() && !tableExists(client, configuration)) {
            createTable(client, configuration);
        }

        return new DynamoSessionRepository(client, configuration);
    }

    @Bean
    public DynamoDbClient dynamoDbClient(DynamoSessionConfiguration configuration) {
        var builder =
                DynamoDbClient.builder()
                        .credentialsProvider(DefaultCredentialsProvider.builder().build())
                        .region(Region.of(configuration.getRegion()));
        if (nonNull(configuration.getEndpoint())) {
            builder.endpointOverride(configuration.getEndpoint());
        }
        return builder.build();
    }

    private static void createTable(
            DynamoDbClient client, DynamoSessionConfiguration configuration) {
        client.createTable(
                CreateTableRequest.builder()
                        .tableName(configuration.getTableName())
                        .billingMode("PAY_PER_REQUEST")
                        .tableClass(TableClass.STANDARD)
                        .attributeDefinitions(
                                AttributeDefinition.builder()
                                        .attributeName(configuration.getSessionIdAttributeName())
                                        .attributeType(ScalarAttributeType.S)
                                        .build())
                        .keySchema(
                                KeySchemaElement.builder()
                                        .attributeName(configuration.getSessionIdAttributeName())
                                        .keyType(KeyType.HASH)
                                        .build())
                        .build());
        client.updateTimeToLive(
                UpdateTimeToLiveRequest.builder()
                        .tableName(configuration.getTableName())
                        .timeToLiveSpecification(
                                TimeToLiveSpecification.builder()
                                        .attributeName(configuration.getTtlAttributeName())
                                        .enabled(true)
                                        .build())
                        .build());
    }

    private static boolean tableExists(
            DynamoDbClient client, DynamoSessionConfiguration configuration) {
        try {
            client.describeTable(
                    DescribeTableRequest.builder().tableName(configuration.getTableName()).build());
            return true;
        } catch (ResourceNotFoundException ignored) {
            return false;
        }
    }
}
