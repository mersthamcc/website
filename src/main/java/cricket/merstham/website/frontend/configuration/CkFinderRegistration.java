package cricket.merstham.website.frontend.configuration;

import com.cksource.ckfinder.servlet.CKFinderServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"cricket.merstham.website.frontend"})
public class CkFinderRegistration {

    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_REQUEST_SIZE = 20 * 1024 * 1024;
    private static final int FILE_SIZE_THRESHOLD = 0;

    public static final String CONNECTOR_PATH = "/administration/components/ckfinder/connector";

    @Bean
    public ServletRegistrationBean<CKFinderServlet> registerCkFinder() {
        ServletRegistrationBean<CKFinderServlet> bean =
                new ServletRegistrationBean<>(new CKFinderServlet(), CONNECTOR_PATH);
        bean.setLoadOnStartup(-1);
        bean.setInitParameters(
                Map.of("scan-path", "cricket.merstham.website.frontend.configuration.ckfinder"));
        bean.setMultipartConfig(new MultipartConfigElement(
                createTempDirectory(),
                MAX_FILE_SIZE,
                MAX_REQUEST_SIZE,
                FILE_SIZE_THRESHOLD));
        return bean;
    }

    private String createTempDirectory() {
        try {
            return Files.createTempDirectory("ckfinder").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
