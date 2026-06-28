package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.AllDuckStatisticEntity;
import cricket.merstham.graphql.entity.AllDuckStatisticEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AllDuckStatisticRepository
        extends CrudRepository<AllDuckStatisticEntity, AllDuckStatisticEntityId>,
                JpaRepository<AllDuckStatisticEntity, AllDuckStatisticEntityId> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT * FROM all_duck_statistics "
                            + "WHERE year = :year "
                            + "ORDER BY ducks DESC, percentage_ducks DESC, runs ASC "
                            + "LIMIT :limit")
    List<AllDuckStatisticEntity> getStatsForYear(int year, int limit);
}
