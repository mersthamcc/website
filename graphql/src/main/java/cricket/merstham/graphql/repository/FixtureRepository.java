package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.TeamEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FixtureRepository
        extends CrudRepository<FixtureEntity, Integer>, JpaRepository<FixtureEntity, Integer> {
    List<FixtureEntity> findByTeamIsAndDateIsBetweenOrderByDateAscStartAsc(
            TeamEntity team, LocalDate start, LocalDate end);

    List<FixtureEntity> findByDateAfterOrderByDateAscStartAsc(LocalDate date, PageRequest page);

    List<FixtureEntity> findByDateIsBetweenOrderByDateAscStartAsc(LocalDate start, LocalDate end);

    List<FixtureEntity> findAllByTeam(TeamEntity team);

    List<FixtureEntity> findAllByDateIn(List<LocalDate> dates);

    List<FixtureEntity> findAllByDateInAndTeamIncludedInSelectionIsTrue(List<LocalDate> dates);

    List<FixtureEntity> findAllByDateAfterAndHomeAwayEquals(LocalDate date, String homeAway);

    @Query(
            value =
                    "SELECT DISTINCT detail->>'competition_id' "
                            + "  FROM fixture "
                            + " WHERE date BETWEEN :start AND :end",
            nativeQuery = true)
    List<Integer> findLeagueIdsForTeamsBetween(
            @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(
            value =
                    "SELECT DISTINCT DATE_PART('year', date) AS Year"
                            + " FROM fixture"
                            + " ORDER BY Year;",
            nativeQuery = true)
    List<Integer> findDistinctYears();

    long countByDateBetween(LocalDate dateStart, LocalDate dateEnd);

    @Query(
            value =
                    "SELECT COUNT(id)"
                            + "  FROM fixture "
                            + " WHERE date BETWEEN :start AND :end "
                            + "   AND (detail ->> 'result') = 'W' "
                            + "   AND CAST(detail ->> 'result_applied_to' AS INTEGER) IN (SELECT id FROM team WHERE status = 'active')",
            nativeQuery = true)
    long countWinsByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
