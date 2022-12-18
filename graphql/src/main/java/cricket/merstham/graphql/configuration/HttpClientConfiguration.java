package cricket.merstham.graphql.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "play-cricket-client")
    public Client getPlayCricketClient() {
        return ClientBuilder.newBuilder().build();
    }
}
