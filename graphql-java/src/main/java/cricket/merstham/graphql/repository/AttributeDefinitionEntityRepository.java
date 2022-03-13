package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.AttributeDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeDefinitionEntityRepository extends JpaRepository<AttributeDefinitionEntity, Integer> {
}