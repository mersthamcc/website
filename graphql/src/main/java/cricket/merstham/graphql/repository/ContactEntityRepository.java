package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ContactEntityRepository
        extends JpaRepository<ContactEntity, Integer>,
                PagingAndSortingRepository<ContactEntity, Integer> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM contact "
                            + "WHERE \"position\" ILIKE '%' || :searchString  || '%' "
                            + "ORDER BY \"position\" DESC "
                            + "LIMIT :length OFFSET :start")
    List<ContactEntity> adminSearch(int start, int length, String searchString);
}
