package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.AllDuckTakersStatisticEntity;
import cricket.merstham.graphql.entity.AllDuckTakersStatisticEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AllDuckTakerStatisticRepository
        extends CrudRepository<AllDuckTakersStatisticEntity, AllDuckTakersStatisticEntityId>,
                JpaRepository<AllDuckTakersStatisticEntity, AllDuckTakersStatisticEntityId> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT * FROM public.all_duck_takers_statistics "
                            + "WHERE year = :year "
                            + "ORDER BY ducks DESC, percentage_ducks DESC, wickets ASC "
                            + "LIMIT :limit")
    List<AllDuckTakersStatisticEntity> getStatsForYear(int year, int limit);
}
