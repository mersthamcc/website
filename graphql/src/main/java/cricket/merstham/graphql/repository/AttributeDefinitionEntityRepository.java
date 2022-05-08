package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.AttributeDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface AttributeDefinitionEntityRepository
        extends JpaRepository<AttributeDefinitionEntity, Integer> {
    AttributeDefinitionEntity findByKey(@NonNull String key);
}
