package cricket.merstham.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GraphqlJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlJavaApplication.class, args);
    }
}
