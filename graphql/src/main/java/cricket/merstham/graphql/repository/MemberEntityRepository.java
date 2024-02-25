package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberEntityRepository
        extends JpaRepository<MemberEntity, Integer>,
                PagingAndSortingRepository<MemberEntity, Integer> {

    @Query(
            value =
                    "SELECT *"
                            + "  FROM \"member\""
                            + " WHERE id NOT IN (SELECT member_id FROM member_identifier WHERE \"name\" = :key)"
                            + "   AND cancelled IS NULL",
            nativeQuery = true)
    List<MemberEntity> findAllWhereIdentifiersDoesNotContainKey(@Param("key") String key);

    List<MemberEntity> findAllByCancelledIsNull();
}
