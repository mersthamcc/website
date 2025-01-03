package cricket.merstham.graphql.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "configuration.bank-details")
@Data
public class BankDetails {
    private String accountName;
    private String accountNumber;
    private String sortCode;
}
