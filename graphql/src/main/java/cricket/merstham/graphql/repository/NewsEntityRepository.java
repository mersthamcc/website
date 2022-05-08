package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NewsEntityRepository
        extends JpaRepository<NewsEntity, Integer>,
                PagingAndSortingRepository<NewsEntity, Integer> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM news "
                            + "WHERE title ILIKE '%' || :searchString  || '%' "
                            + "OR body ILIKE '%' || :searchString  || '%' "
                            + "ORDER BY publish_date DESC "
                            + "LIMIT :length OFFSET :start")
    List<NewsEntity> adminSearch(int start, int length, String searchString);
}
