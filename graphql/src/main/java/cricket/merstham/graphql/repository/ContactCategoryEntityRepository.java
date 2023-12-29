package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.ContactCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ContactCategoryEntityRepository
        extends JpaRepository<ContactCategoryEntity, Integer>,
                PagingAndSortingRepository<ContactCategoryEntity, Integer> {
    @Query(
            nativeQuery = true,
            value =
                    "SELECT *  FROM contact_category "
                            + "WHERE title ILIKE '%' || :searchString  || '%' "
                            + "ORDER BY sort_order ASC, title ASC "
                            + "LIMIT :length OFFSET :start")
    List<ContactCategoryEntity> adminSearch(int start, int length, String searchString);
}
