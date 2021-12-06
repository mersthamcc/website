package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.loader.ConfigLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CkFinderConfiguration {
    @Bean
    public ConfigLoader getConfigLoader(
            @Value("#{environment.CKFINDER_LICENCE_NAME}") String licenseName,
            @Value("#{environment.CKFINDER_LICENCE_KEY}") String licenseKey,
            @Value("#{environment.RESOURCES_BASE_URL}") String resourceUrl,
            @Value("#{environment.RESOURCES_BASE_DIRECTORY}") String resourceDirectory) {
        return new CkFinderConfigLoader(licenseName, licenseKey, resourceUrl, resourceDirectory);
    }
}
