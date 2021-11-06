package cricket.merstham.website.frontend.configuration;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.servlet.CKFinderServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.util.Map;

@Configuration
public class CkFinderConfiguration {
    @Bean
    public ServletRegistrationBean<CKFinderServlet> ckFinderRegistrationBean() {
        var registration = new ServletRegistrationBean<>(new CKFinderServlet(), "/administration/components/finder/*");
        registration.setMultipartConfig(new MultipartConfigElement("/tmp/upload"));
        registration.setInitParameters(Map.of(
                "scan-path", "cricket.merstham.website.frontend.configuration"
        ));
        return registration;
    }

    @Bean
    public Config baseConfig() {
        return new Config();
    }
}
