package cricket.merstham.graphql.config;

import cricket.merstham.graphql.jpa.JpaEncryptedJsonbType;
import cricket.merstham.graphql.jpa.JpaJsonbType;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.TypeContributorList;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class HibernateConfiguration implements HibernatePropertiesCustomizer {

    public static final String ENCRYPTED_JSON_TYPE = "JpaEncryptedJsonbType";
    public static final String JSON_TYPE = "JpaJsonbType";

    @Value("${configuration.database-secret}")
    private String secret;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(EntityManagerFactoryBuilderImpl.TYPE_CONTRIBUTORS,
                (TypeContributorList) () -> List.of(
                        (TypeContributions typeContributions, ServiceRegistry serviceRegistry) ->
                                typeContributions.contributeType(
                                        new JpaEncryptedJsonbType(secret),
                                        ENCRYPTED_JSON_TYPE),
                        (TypeContributions typeContributions, ServiceRegistry serviceRegistry) ->
                                typeContributions.contributeType(
                                        new JpaJsonbType(),
                                        JSON_TYPE)));
    }
}