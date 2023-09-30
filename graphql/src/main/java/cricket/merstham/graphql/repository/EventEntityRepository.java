package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EventEntityRepository
        extends JpaRepository<EventEntity, Integer>,
                PagingAndSortingRepository<EventEntity, Integer> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM event "
                            + "WHERE title ILIKE '%' || :searchString  || '%' "
                            + "   OR body ILIKE '%' || :searchString  || '%' "
                            + "ORDER BY event_date DESC "
                            + "LIMIT :length OFFSET :start")
    List<EventEntity> adminSearch(int start, int length, String searchString);
}
