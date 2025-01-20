package cricket.merstham.graphql.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.config.SqsListenerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsConfiguration {
    @Bean
    SqsListenerConfigurer configurer(ObjectMapper objectMapper) {
        return registrar -> registrar.setObjectMapper(objectMapper);
    }
}
