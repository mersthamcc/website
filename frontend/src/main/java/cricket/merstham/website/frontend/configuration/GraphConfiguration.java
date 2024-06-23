package cricket.merstham.website.frontend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "graph")
public class GraphConfiguration {

    private URI graphUri;

    public URI getGraphUri() {
        return graphUri;
    }

    public GraphConfiguration setGraphUri(URI graphUri) {
        this.graphUri = graphUri;
        return this;
    }
}
