package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VenueRepository
        extends JpaRepository<VenueEntity, String>,
                PagingAndSortingRepository<VenueEntity, String> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM venue "
                            + "WHERE name ILIKE '%' || :searchString  || '%' OR slug ILIKE '%' || :searchString  || '%'"
                            + "ORDER BY sort_order ASC, name ASC "
                            + "LIMIT :length OFFSET :start")
    List<VenueEntity> adminSearch(int start, int length, String searchString);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM venue "
                            + "WHERE venue.show_on_menu = TRUE "
                            + "ORDER BY sort_order ASC, name ASC")
    List<VenueEntity> venuesForMenu();

    VenueEntity findByPlayCricketId(long playCricketId);
}
