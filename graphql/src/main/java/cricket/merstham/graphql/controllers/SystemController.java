package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.dto.Configuration;
import cricket.merstham.shared.dto.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller("adminSystemController")
public class SystemController {

    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";

    private final Environment environment;

    @Autowired
    public SystemController(Environment environment) {
        this.environment = environment;
    }

    @QueryMapping
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public Configuration config() {
        var config = new ArrayList<KeyValuePair>();
        for (PropertySource<?> source : ((AbstractEnvironment) environment).getPropertySources()) {
            if (source instanceof OriginTrackedMapPropertySource sourceMap) {
                var keys = sourceMap.getSource().keySet();
                keys.forEach(
                        s ->
                                config.add(
                                        KeyValuePair.builder()
                                                .key(s)
                                                .value(environment.getProperty(s))
                                                .build()));
            }
        }
        return Configuration.builder()
                .profiles(List.of(environment.getActiveProfiles()))
                .properties(config)
                .environment(
                        ((AbstractEnvironment) environment)
                                .getSystemEnvironment().entrySet().stream()
                                        .map(
                                                e ->
                                                        KeyValuePair.builder()
                                                                .key(e.getKey())
                                                                .value(e.getValue().toString())
                                                                .build())
                                        .toList())
                .build();
    }
}
