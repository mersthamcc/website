package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Integer> {

    List<PaymentEntity> findPaymentEntitiesByReconciledIsFalseAndCollectedIsTrue();
}
