package cricket.merstham.graphql.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.AwsIamAuthentication;
import org.springframework.vault.authentication.AwsIamAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "configuration.vault")
@Data
public class VaultConfiguration extends AbstractVaultConfiguration {

    private String url;
    private String authType;
    private String token;
    private String role;
    private String environmentSecretsPath;

    @NotNull
    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(getUrl());
    }

    @NotNull
    @Override
    public ClientAuthentication clientAuthentication() {
        return switch (getAuthType()) {
            case "TOKEN" -> new TokenAuthentication(getToken());
            case "AWS_IAM" -> new AwsIamAuthentication(
                    AwsIamAuthenticationOptions.builder()
                            .credentialsProvider(DefaultCredentialsProvider.create())
                            .role(getRole())
                            .build(),
                    restOperations());
            default -> throw new RuntimeException("Unsupported authentication type");
        };
    }
}
