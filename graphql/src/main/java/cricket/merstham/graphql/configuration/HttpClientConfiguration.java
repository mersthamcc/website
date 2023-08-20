package cricket.merstham.graphql.configuration;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "play-cricket-client")
    public Client getPlayCricketClient() {
        return ClientBuilder.newBuilder().build();
    }
}
