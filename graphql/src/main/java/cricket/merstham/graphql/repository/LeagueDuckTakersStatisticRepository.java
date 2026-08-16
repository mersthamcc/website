package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.LeagueDuckTakersStatisticEntity;
import cricket.merstham.graphql.entity.LeagueDuckTakersStatisticEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueDuckTakersStatisticRepository
        extends CrudRepository<LeagueDuckTakersStatisticEntity, LeagueDuckTakersStatisticEntityId>,
                JpaRepository<LeagueDuckTakersStatisticEntity, LeagueDuckTakersStatisticEntityId> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT * FROM league_duck_takers_statistics "
                            + "WHERE year = :year "
                            + "ORDER BY ducks DESC, percentage_ducks DESC, wickets ASC "
                            + "LIMIT :limit")
    List<LeagueDuckTakersStatisticEntity> getStatsForYear(int year, int limit);
}
