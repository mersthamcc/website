package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.FixturePlayerSummaryEntity;
import cricket.merstham.graphql.entity.FixturePlayerSummaryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface FixturePlayerSummaryEntityRepository
        extends CrudRepository<FixturePlayerSummaryEntity, FixturePlayerSummaryEntityId>,
                JpaRepository<FixturePlayerSummaryEntity, FixturePlayerSummaryEntityId> {}
