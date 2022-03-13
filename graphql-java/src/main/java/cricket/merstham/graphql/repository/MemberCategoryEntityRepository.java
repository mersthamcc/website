package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCategoryEntityRepository extends JpaRepository<MemberCategoryEntity, Integer> {
}