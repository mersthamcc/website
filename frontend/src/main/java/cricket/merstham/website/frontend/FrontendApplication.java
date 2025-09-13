package cricket.merstham.website.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Locale;

@SpringBootApplication
@EnableCaching
public class FrontendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        SpringApplication.run(FrontendApplication.class, args);
    }
}
