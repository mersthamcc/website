package cricket.merstham.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;

@SpringBootApplication
@EnableScheduling
public class GraphqlJavaApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        SpringApplication.run(GraphqlJavaApplication.class, args);
    }
}
