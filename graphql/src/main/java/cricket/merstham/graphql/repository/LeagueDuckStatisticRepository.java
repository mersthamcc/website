package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.LeagueDuckStatisticEntity;
import cricket.merstham.graphql.entity.LeagueDuckStatisticEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueDuckStatisticRepository
        extends CrudRepository<LeagueDuckStatisticEntity, LeagueDuckStatisticEntityId>,
                JpaRepository<LeagueDuckStatisticEntity, LeagueDuckStatisticEntityId> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT * FROM league_duck_statistics "
                            + "WHERE year = :year "
                            + "ORDER BY ducks DESC, percentage_ducks DESC, runs ASC "
                            + "LIMIT :limit")
    List<LeagueDuckStatisticEntity> getStatsForYear(int year, int limit);
}
