package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.StaticPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StaticPageRepository
        extends JpaRepository<StaticPageEntity, String>,
                PagingAndSortingRepository<StaticPageEntity, String> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM static_page "
                            + "WHERE title ILIKE '%' || :searchString  || '%' OR slug ILIKE '%' || :searchString  || '%'"
                            + "ORDER BY sort_order ASC, title ASC "
                            + "LIMIT :length OFFSET :start")
    List<StaticPageEntity> adminSearch(int start, int length, String searchString);
}
