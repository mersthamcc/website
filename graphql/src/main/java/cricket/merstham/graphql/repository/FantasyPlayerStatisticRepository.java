package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.FantasyPlayerStatisticEntity;
import cricket.merstham.graphql.entity.FantasyPlayerStatisticEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FantasyPlayerStatisticRepository
        extends CrudRepository<FantasyPlayerStatisticEntity, Integer>,
                JpaRepository<FantasyPlayerStatisticEntity, Integer> {
    Optional<FantasyPlayerStatisticEntity> findDistinctByIdIs(FantasyPlayerStatisticEntityId id);
}
