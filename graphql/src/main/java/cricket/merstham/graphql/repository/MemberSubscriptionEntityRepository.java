package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface MemberSubscriptionEntityRepository
        extends JpaRepository<MemberSubscriptionEntity, MemberSubscriptionEntityId>,
                PagingAndSortingRepository<MemberSubscriptionEntity, MemberSubscriptionEntityId> {

    @Modifying
    @Query(
            value =
                    "UPDATE member_subscription"
                            + "   SET member_id = :newMember"
                            + " WHERE member_id = :oldMember",
            nativeQuery = true)
    int migrateSubscriptionToNewMember(
            @Param("oldMember") int oldMember, @Param("newMember") int newMember);
}
