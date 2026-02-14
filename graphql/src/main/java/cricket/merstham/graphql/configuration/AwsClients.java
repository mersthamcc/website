package cricket.merstham.graphql.configuration;

import cricket.merstham.graphql.configuration.interceptors.AwsSigningInterceptor;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Bean
    public SesV2Client sesV2Client(@Value("${configuration.region}") String region) {
        return SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Bean
    @Named("aws-signing-api-client")
    public RestTemplate getApiGatewayRestTemplate(@Value("${configuration.region}") String region) {
        return new RestTemplateBuilder()
                .interceptors(
                        new AwsSigningInterceptor(
                                DefaultCredentialsProvider.builder().build(),
                                region,
                                "execute-api"))
                .build();
    }
}
