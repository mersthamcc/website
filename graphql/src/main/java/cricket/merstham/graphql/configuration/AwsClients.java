package cricket.merstham.graphql.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class AwsClients {

    @Bean
    public LambdaClient getLambdaClient(@Value("${configuration.region}") String region) {
        return LambdaClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(region))
                .build();
    }

    @Bean
    public CognitoIdentityProviderClient getCognitoIdentityProviderClient(
            @Value("${configuration.region:#{null}}") String region) {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public SesV2Client sesV2Client(@Value("${configuration.region}") String region) {
        return SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
