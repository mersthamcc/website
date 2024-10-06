package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.loader.ConfigLoader;
import jakarta.ws.rs.core.UriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.micrometer.common.util.StringUtils.isBlank;

@Configuration
public class CkFinderConfiguration {
    @Bean
    public ConfigLoader getConfigLoader(
            @Value("#{environment.CKFINDER_LICENCE_NAME}") String licenseName,
            @Value("#{environment.CKFINDER_LICENCE_KEY}") String licenseKey,
            @Value("${resources.base-url}") String resourceUrl,
            @Value("${resources.base-directory}") String resourceDirectory,
            @Value("${resources.bucket-prefix}") String bucketPrefix) {
        if (isBlank(bucketPrefix))
            return new CkFinderConfigLoader(
                    licenseName, licenseKey, resourceUrl, resourceDirectory);
        var resourceUri = UriBuilder.fromUri(resourceUrl).path(bucketPrefix).build();
        return new CkFinderConfigLoader(
                licenseName, licenseKey, resourceUri.toString(), resourceDirectory);
    }
}
