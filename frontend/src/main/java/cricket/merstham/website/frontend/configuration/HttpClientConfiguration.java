package cricket.merstham.website.frontend.configuration;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "graphqlClient")
    public Client getGraphQLClient() {
        return ClientBuilder.newBuilder().build();
    }
}
