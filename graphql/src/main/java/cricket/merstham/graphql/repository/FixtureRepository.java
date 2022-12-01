package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface FixtureRepository
        extends CrudRepository<FixtureEntity, Integer>,
        JpaRepository<FixtureEntity, Integer> {
    List<FixtureEntity> findByTeamIdAndDateAfter(TeamEntity teamId, LocalDate date);
}
