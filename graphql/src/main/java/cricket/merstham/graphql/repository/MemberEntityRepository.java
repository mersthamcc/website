package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, Integer> {}
