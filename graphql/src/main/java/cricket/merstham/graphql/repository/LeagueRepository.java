package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.LeagueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeagueRepository
        extends CrudRepository<LeagueEntity, Integer>, JpaRepository<LeagueEntity, Integer> {

    @Query(
            value =
                    "SELECT *  "
                            + "  FROM league "
                            + " WHERE jsonb_exists_any(jsonb_path_query_array(\"table\", '$.values[*].team_id'), array[cast(:teamId as text)])",
            nativeQuery = true)
    List<LeagueEntity> findLeaguesForTeamId(@Param("teamId") int teamId);
}
