package cricket.merstham.website.frontend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "graphql-client")
    public Client getGraphQLClient() {
        return ClientBuilder.newBuilder().build();
    }
}
