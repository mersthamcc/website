package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PlayerSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PlayerSummaryRepository
        extends CrudRepository<PlayerSummaryEntity, Integer>,
                JpaRepository<PlayerSummaryEntity, Integer> {}
