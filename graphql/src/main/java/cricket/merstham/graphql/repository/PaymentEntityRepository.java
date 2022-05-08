package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Integer> {}
