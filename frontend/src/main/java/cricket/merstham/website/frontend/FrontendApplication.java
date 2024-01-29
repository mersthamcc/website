package cricket.merstham.website.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Locale;

@SpringBootApplication
@EnableCaching
public class FrontendApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        SpringApplication.run(FrontendApplication.class, args);
    }
}
