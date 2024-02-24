package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Integer> {

    List<PaymentEntity> findPaymentEntitiesByReconciledIsFalseAndCollectedIsTrue();

    Optional<PaymentEntity> findByTypeAndReference(String type, String reference);
}
