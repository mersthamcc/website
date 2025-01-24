package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberEntityRepository
        extends JpaRepository<MemberEntity, Integer>,
                PagingAndSortingRepository<MemberEntity, Integer> {

    Optional<MemberEntity> findByIdAndOwnerUserId(Integer id, String ownerUserId);

    List<MemberEntity> findAllByOwnerUserId(String ownerUserId);

    @Query(
            value =
                    "SELECT *"
                            + "  FROM \"member\""
                            + " WHERE id NOT IN (SELECT member_id FROM member_identifier WHERE \"name\" = :key)"
                            + "   AND cancelled IS NULL",
            nativeQuery = true)
    List<MemberEntity> findAllWhereIdentifiersDoesNotContainKey(@Param("key") String key);

    List<MemberEntity> findAllByCancelledIsNull();

    Optional<MemberEntity> findFirstByUuid(String uuid);

    Optional<MemberEntity> findByUuidAndOwnerUserId(String uuid, String ownerUserId);

    List<MemberEntity> findAllByOwnerUserIdAndCancelledIsNull(String subject);
}
