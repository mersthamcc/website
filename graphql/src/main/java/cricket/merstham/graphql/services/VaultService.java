package cricket.merstham.graphql.services;

import cricket.merstham.graphql.configuration.VaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Service
public class VaultService {

    private final VaultConfiguration configuration;

    @Autowired
    public VaultService(VaultConfiguration configuration) {
        this.configuration = configuration;
    }

    public Map<String, Object> get(String path) {
        var template =
                new VaultTemplate(
                        configuration.vaultEndpoint(), configuration.clientAuthentication());
        var result = template.read(path);

        return requireNonNull(result).getData();
    }

    public Map<String, Object> write(String path, Map<String, Object> data) {
        var template =
                new VaultTemplate(
                        configuration.vaultEndpoint(), configuration.clientAuthentication());
        var result = template.write(path, data);

        if (isNull(result)) return Map.of();
        return requireNonNull(result).getData();
    }
}
