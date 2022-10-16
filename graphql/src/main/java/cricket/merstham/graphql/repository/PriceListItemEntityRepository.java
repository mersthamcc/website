package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PricelistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceListItemEntityRepository
        extends JpaRepository<PricelistItemEntity, Integer> {}
