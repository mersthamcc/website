package cricket.merstham.website.frontend.configuration;

import com.gocardless.GoCardlessClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.gocardless.GoCardlessClient.Environment.LIVE;
import static com.gocardless.GoCardlessClient.Environment.SANDBOX;

@Configuration
public class GoCardlessConfiguration {

    @Bean
    public GoCardlessClient goCardlessClient(
            @Value("${payments.gocardless.access-token}") String accessToken,
            @Value("${payments.gocardless.sandbox}") boolean sandbox) {
        return GoCardlessClient.newBuilder(accessToken)
                .withEnvironment(sandbox ? SANDBOX : LIVE)
                .build();
    }
}
