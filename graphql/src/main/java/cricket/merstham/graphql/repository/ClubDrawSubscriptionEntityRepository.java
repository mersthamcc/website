package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.ClubDrawSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubDrawSubscriptionEntityRepository
        extends JpaRepository<ClubDrawSubscriptionEntity, Integer> {
    List<ClubDrawSubscriptionEntity> findAllByOwnerUserId(String subjectId);
}
