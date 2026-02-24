package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.StaticDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StaticDataEntityRepository
        extends JpaRepository<StaticDataEntity, Integer>,
                PagingAndSortingRepository<StaticDataEntity, Integer> {

    StaticDataEntity findByPath(String path);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM static_data "
                            + "WHERE path ILIKE '%' || :searchString  || '%' "
                            + "ORDER BY path ASC, content_type ASC, status_code ASC "
                            + "LIMIT :length OFFSET :start")
    List<StaticDataEntity> adminSearch(int start, int length, String searchString);
}
